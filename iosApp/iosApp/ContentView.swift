import UIKit
import SwiftUI
import ComposeApp
import GoogleMaps
import GoogleMapsUtils

struct GoogleMapView: UIViewRepresentable {
    
    private let googleMapsVM: GoogleMapsViewModel
    private let momentsVM: MomentsViewModel
    private let profileVM: ProfileViewModel
    
    init() {
        googleMapsVM = ViewModelProvider().getGoogleMapsViewModel()
        momentsVM = ViewModelProvider().getMomentsViewModel()
        profileVM = ViewModelProvider().getProfileViewModel()
    }
    
    func makeUIView(context: Context) -> GMSMapView {
        let camera = GMSCameraPosition(latitude: 33.6, longitude: -7.6, zoom: 6.0)
        let mapView = GMSMapView(frame: .zero, camera: camera)
        mapView.delegate = context.coordinator
        mapView.mapType = .terrain
        
        // Set up coordinator
        context.coordinator.parent = self
        context.coordinator.mapView = mapView
        context.coordinator.googleMapsVM = googleMapsVM
        context.coordinator.momentsVM = momentsVM
        context.coordinator.profileVM = profileVM
        
        // Load data
        profileVM.getProfile()
        googleMapsVM.loadGlobalMoments()
        
        // Start observation
        context.coordinator.startObservation()
        
        print("🗺️ GoogleMapView created, starting observation")
        return mapView
    }
    
    func updateUIView(_ uiView: GMSMapView, context: Context) {
        // Updates handled by coordinator
    }
    
    func makeCoordinator() -> Coordinator {
        return Coordinator()
    }
    
    // MARK: - Coordinator
    class Coordinator: NSObject, GMSMapViewDelegate {
        var parent: GoogleMapView?
        var mapView: GMSMapView?
        var googleMapsVM: GoogleMapsViewModel?
        var momentsVM: MomentsViewModel?
        var profileVM: ProfileViewModel?
        
        private var currentHeatmapLayer: GMUHeatmapTileLayer?
        private var observationTimer: Timer?
        private var lastProcessedMomentsCount = -1
        private var observationStarted = false
        
        func startObservation() {
            guard let googleMapsVM = googleMapsVM, !observationStarted else { return }
            
            print("🔍 Starting observation")
            observationStarted = true
            
            // Initial check
            checkStateAndUpdate()
            
            // Use a timer to check state changes
            observationTimer = Timer.scheduledTimer(withTimeInterval: 2.0, repeats: true) { [weak self] _ in
                self?.checkStateAndUpdate()
            }
        }
        
        private func checkStateAndUpdate() {
            guard let googleMapsVM = googleMapsVM else { return }
            
            // Get the current state
            let currentState = googleMapsVM.globalMomentsState.value
            
            // Extract moments using multiple approaches
            let moments = extractMoments(from: currentState)
            
            // Update heatmap if we have new data
            if !moments.isEmpty && moments.count != lastProcessedMomentsCount {
                print("🎨 New data detected (\(moments.count) moments), updating heatmap")
                updateHeatmap(with: moments)
                lastProcessedMomentsCount = moments.count
            }
        }
        
        private func extractMoments(from state: Any) -> [MomentModel] {
            // Approach 1: Check if it's our custom wrapper
            if let successState = state as? UiStateSuccess {
                return successState.data
            }
            
            // Approach 2: Use reflection to extract data
            let mirror = Mirror(reflecting: state)
            
            for child in mirror.children {
                let label = child.label ?? ""
                
                // Look for 'data' property
                if label == "data", let moments = child.value as? [MomentModel] {
                    return moments
                }
                
                // Look for 'value' property that might contain data
                if label == "value" {
                    let valueMirror = Mirror(reflecting: child.value)
                    for valueChild in valueMirror.children {
                        if valueChild.label == "data", let moments = valueChild.value as? [MomentModel] {
                            return moments
                        }
                    }
                }
            }
            
            // Approach 3: Check if it's a KMP Success state
            let stateString = String(describing: type(of: state))
            if stateString.contains("Success") {
                print("⚠️ Found Success state but couldn't extract data directly")
                logStateProperties(state)
            }
            
            return []
        }
        
        private func logStateProperties(_ state: Any) {
            print("🐛 Detailed state inspection for type: \(type(of: state))")
            let mirror = Mirror(reflecting: state)
            
            for child in mirror.children {
                let label = child.label ?? "unnamed"
                let valueType = type(of: child.value)
                print("  - \(label): \(valueType)")
                
                // Log string representation for debugging
                if let stringConvertible = child.value as? CustomStringConvertible {
                    print("    Value: \(stringConvertible.description)")
                }
            }
        }
        
        private func updateHeatmap(with moments: [MomentModel]) {
            guard let mapView = mapView else { return }
            
            // Remove existing heatmap
            currentHeatmapLayer?.map = nil
            
            // Create heatmap data with validation
            let heatmapData: [GMUWeightedLatLng] = moments.compactMap { moment in
                let lat = moment.geoLocation.latitude
                let lng = moment.geoLocation.longitude
                
                guard (-90...90).contains(lat) && (-180...180).contains(lng) else {
                    print("❌ Invalid coordinates: lat=\(lat), lng=\(lng)")
                    return nil
                }
                
                return GMUWeightedLatLng(
                    coordinate: CLLocationCoordinate2D(latitude: lat, longitude: lng),
                    intensity: 1.0
                )
            }
            
            guard !heatmapData.isEmpty else {
                print("❌ No valid heatmap data to display")
                return
            }
            
            print("📍 Creating heatmap with \(heatmapData.count) valid points")
            
            // Create heatmap layer
            let heatmapLayer = GMUHeatmapTileLayer()
            heatmapLayer.weightedData = heatmapData
            heatmapLayer.radius = 50
            heatmapLayer.opacity = 0.7
            
            // Create gradient
            let gradientColors: [UIColor] = [
                UIColor(red: 1.0, green: 1.0, blue: 0.0, alpha: 0.0),
                UIColor(red: 1.0, green: 1.0, blue: 0.0, alpha: 1.0),
                UIColor(red: 1.0, green: 0.647, blue: 0.0, alpha: 1.0),
                UIColor(red: 1.0, green: 0.0, blue: 0.0, alpha: 1.0)
            ]
            let gradientStartPoints: [NSNumber] = [0.0, 0.3, 0.7, 1.0]
            
            heatmapLayer.gradient = GMUGradient(
                colors: gradientColors,
                startPoints: gradientStartPoints,
                colorMapSize: 256
            )
            
            heatmapLayer.map = mapView
            currentHeatmapLayer = heatmapLayer
            
            print("✅ Heatmap successfully updated!")
        }
        
        // MARK: - GMSMapViewDelegate
        func mapView(_ mapView: GMSMapView, didTapAt coordinate: CLLocationCoordinate2D) {
            guard let googleMapsVM = googleMapsVM,
                  let momentsVM = momentsVM,
                  let profileVM = profileVM else { return }
            
            print("🎯 Map tapped at: \(coordinate)")
            
            // Get moments
            let currentState = googleMapsVM.globalMomentsState.value
            let moments = extractMoments(from: currentState)
            
            guard !moments.isEmpty else {
                print("❌ No moments available for tap handling")
                return
            }
            
            // Handle map click
            let momentsAround = handleMapClick(
                clickedCoordinate: coordinate,
                moments: moments,
                zoomLevel: mapView.camera.zoom
            )
            
            guard !momentsAround.isEmpty else {
                print("No moments found around tap location")
                return
            }
            
            print("✅ Found \(momentsAround.count) moments around tap")
            
            // Update MomentsViewModel
            momentsVM.moments = [momentsAround]
            momentsVM.selectedIndex = 0
            
            // Set myID based on profile state
           
            
            print("🎯 Ready to navigate to moments screen")
        }
        
        // MARK: - Helper Methods
        private func handleMapClick(
            clickedCoordinate: CLLocationCoordinate2D,
            moments: [MomentModel],
            zoomLevel: Float
        ) -> [MomentModel] {
            let baseRadius = 20000.0
            let radiusInMeters = max(50.0, min(50000.0, baseRadius / pow(Double(zoomLevel) / 6.0, 1.5)))
            
            return moments.filter { moment in
                haversineDistance(
                    lat1: clickedCoordinate.latitude,
                    lon1: clickedCoordinate.longitude,
                    lat2: moment.geoLocation.latitude,
                    lon2: moment.geoLocation.longitude
                ) <= radiusInMeters
            }
        }
        
        private func haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double) -> Double {
            let R = 6371000.0
            let dLat = (lat2 - lat1).degreesToRadians
            let dLon = (lon2 - lon1).degreesToRadians
            let a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(lat1.degreesToRadians) *
                    cos(lat2.degreesToRadians) *
                    sin(dLon / 2) * sin(dLon / 2)
            let c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return R * c
        }
        
        deinit {
            observationTimer?.invalidate()
            print("🗑️ GoogleMapView Coordinator deinitialized")
        }
    }
}

private extension Double {
    var degreesToRadians: Double { self * .pi / 180.0 }
}

// MARK: - UiState wrapper helpers for Swift
protocol UiStateValue {}

struct UiStateSuccess: UiStateValue {
    let data: [MomentModel]
}

struct ProfileUiStateSuccess: UiStateValue {
    let profile: ProfileModel
}




struct CameraSwiftUIView: UIViewControllerRepresentable {

    var onMediaCaptured: ((Data, String) -> Void)? // ByteArray + fileName

    func makeUIViewController(context: Context) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.sourceType = .camera
        picker.mediaTypes = ["public.image", "public.movie"] // ✅ allow image & video
        picker.videoQuality = .typeHigh
        picker.allowsEditing = false
        picker.delegate = context.coordinator
        picker.modalPresentationStyle = .fullScreen
        return picker
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(parent: self)
    }

    class Coordinator: NSObject, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
        let parent: CameraSwiftUIView

        init(parent: CameraSwiftUIView) {
            self.parent = parent
        }

        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
            picker.dismiss(animated: true)

            if let image = info[.originalImage] as? UIImage,
               let data = image.jpegData(compressionQuality: 0.8) {
                parent.onMediaCaptured?(data, "photo.jpg")
            } else if let videoURL = info[.mediaURL] as? URL {
                do {
                    let data = try Data(contentsOf: videoURL)
                    parent.onMediaCaptured?(data, "video.mov")
                } catch {
                    print("❌ Failed to load video data: \(error)")
                }
            }
        }

        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            picker.dismiss(animated: true)
        }
    }
}

extension Data {
    func toKotlinByteArray() -> KotlinByteArray {
        let byteArray = KotlinByteArray(size: Int32(self.count))
        self.withUnsafeBytes { rawBufferPointer in
            let bufferPointer = rawBufferPointer.bindMemory(to: Int8.self)
            for (index, byte) in bufferPointer.enumerated() {
                byteArray.set(index: Int32(index), value: byte)
            }
        }
        return byteArray
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(

            mapUIViewController: {
                UIHostingController(rootView: GoogleMapView())
            },
            cameraUIViewController: { onMediaCaptured in
                UIHostingController(
                    rootView: CameraSwiftUIView(
                        onMediaCaptured: { data, fileName in
                            // ✅ This is the callback to Kotlin
                            onMediaCaptured(data.toKotlinByteArray(), fileName)
                        }
                    )
                )
            }

        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}
