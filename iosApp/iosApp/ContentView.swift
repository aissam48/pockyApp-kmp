import UIKit
import SwiftUI
import ComposeApp
import GoogleMaps
import GoogleMapsUtils

struct GoogleMapView: UIViewRepresentable {

    private let googleMapsViewModel: GoogleMapsViewModel

    init() {
        // This should work now since Koin is already initialized
        googleMapsViewModel = ViewModelProvider().getGoogleMapsViewModel()

    }

    func makeUIView(context: Context) -> GMSMapView {
        let options = GMSMapViewOptions()

        googleMapsViewModel.loadGlobalMoments()

        options.camera = GMSCameraPosition.camera(
            withLatitude: 33.5731,
            longitude: -7.5898,
            zoom: 12.0
        )
        let mapView = GMSMapView(options: options)

        // Creates a marker in the center of the map.


        // Add heatmap
        addHeatmap(to: mapView)

        return mapView
    }

    func updateUIView(_ uiView: GMSMapView, context: Context) {
    }

    private func addHeatmap(to mapView: GMSMapView) {
        // Sample data points for heatmap (you can replace with your actual data)
        let heatmapData: [GMUWeightedLatLng] = [
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 33.5731, longitude: -7.5898), intensity: 1.0), // Casablanca
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 33.7000, longitude: -7.3500), intensity: 0.8), // Between Casa & Rabat
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 33.8500, longitude: -7.1000), intensity: 0.9), // Midpoint closer to Rabat
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 33.9500, longitude: -6.9500), intensity: 0.7),
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 34.0209, longitude: -6.8416), intensity: 0.6), // Rabat
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 33.8000, longitude: -7.2000), intensity: 0.8),
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 33.9000, longitude: -7.0000), intensity: 0.5),
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 33.7500, longitude: -7.3000), intensity: 0.9),
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 33.8200, longitude: -7.1500), intensity: 0.7),
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 33.8800, longitude: -7.0500), intensity: 0.8)
        ]


        // Create heatmap layer
        let heatmapLayer = GMUHeatmapTileLayer()
        heatmapLayer.weightedData = heatmapData

        // Customize heatmap appearance (optional)
        heatmapLayer.radius = 80 // Radius of influence for each point
        heatmapLayer.opacity = 0.7 // Transparency of the heatmap

        let gradientColors: [UIColor] = [.yellow, .orange, .red]
        let gradientStartPoints: [NSNumber] = [0.2, 0.5, 1.0]

        heatmapLayer.gradient = GMUGradient(
            colors: gradientColors,
            startPoints: gradientStartPoints,
            colorMapSize: 256
        )

        // Add heatmap to map
        heatmapLayer.map = mapView
    }
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
