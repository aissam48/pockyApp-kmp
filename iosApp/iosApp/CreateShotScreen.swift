import SwiftUI
import AVKit
import AVFoundation
import PhotosUI
import CoreLocation

struct CreateShotScreen: View {
    @Environment(\.presentationMode) var presentationMode
    @StateObject private var viewModel = CreateShotViewModel()
    @StateObject private var locationManager = LocationManager()
    
    @State private var selectedVideo: PhotosPickerItem?
    @State private var videoURL: URL?
    @State private var videoData: Data?
    @State private var player: AVPlayer?
    
    @State private var isLocationEnabled = false
    @State private var showingVideoPicker = false
    @State private var showingSuccessDialog = false
    @State private var showingErrorDialog = false
    @State private var showingLocationDialog = false
    @State private var dialogTitle = ""
    
    var body: some View {
        ZStack {
            Color(red: 0.1, green: 0.1, blue: 0.1)
                .ignoresSafeArea()
            
            VStack(spacing: 0) {
                // Video Preview Section
                videoPreviewSection
                    .frame(maxHeight: .infinity)
                
                // Controls Section
                controlsSection
            }
            
            // Close Button
            VStack {
                HStack {
                    Button(action: {
                        presentationMode.wrappedValue.dismiss()
                    }) {
                        Image(systemName: "xmark")
                            .font(.system(size: 16, weight: .medium))
                            .foregroundColor(.black)
                            .frame(width: 40, height: 40)
                            .background(Color.white.opacity(0.5))
                            .clipShape(Circle())
                    }
                    Spacer()
                }
                .padding(16)
                Spacer()
            }
        }
        .sheet(isPresented: $showingVideoPicker) {
            PhotosPicker(
                selection: $selectedVideo,
                matching: .videos,
                photoLibrary: .shared()
            ) {
                Text("Select Video")
            }
        }
        .onChange(of: selectedVideo) { newItem in
            loadVideo(from: newItem)
        }
        .onChange(of: isLocationEnabled) { enabled in
            if enabled {
                locationManager.requestLocation()
            }
        }
        .alert("Success", isPresented: $showingSuccessDialog) {
            Button("OK") {
                presentationMode.wrappedValue.dismiss()
            }
        } message: {
            Text(dialogTitle)
        }
        .alert("Error", isPresented: $showingErrorDialog) {
            Button("OK") { }
        } message: {
            Text(dialogTitle)
        }
        .alert("Location", isPresented: $showingLocationDialog) {
            Button("OK") { }
        } message: {
            Text(dialogTitle)
        }
        .onReceive(viewModel.$uploadState) { state in
            handleUploadState(state)
        }
        .onReceive(locationManager.$locationError) { error in
            if let error = error {
                dialogTitle = error
                showingLocationDialog = true
                isLocationEnabled = false
            }
        }
    }
    
    private var videoPreviewSection: some View {
        RoundedRectangle(cornerRadius: 20)
            .fill(videoURL != nil ? Color.clear : Color(red: 0.16, green: 0.16, blue: 0.16))
            .overlay(
                Group {
                    if let videoURL = videoURL, let player = player {
                        VideoPlayer(player: player)
                            .clipShape(RoundedRectangle(cornerRadius: 20))
                            .onAppear {
                                player.seek(to: .zero)
                                player.isMuted = true
                            }
                            .overlay(
                                // Play overlay
                                Button(action: {
                                    if player.timeControlStatus == .playing {
                                        player.pause()
                                    } else {
                                        player.play()
                                    }
                                }) {
                                    Image(systemName: "play.fill")
                                        .font(.system(size: 24))
                                        .foregroundColor(.white)
                                        .frame(width: 60, height: 60)
                                        .background(Color.black.opacity(0.5))
                                        .clipShape(Circle())
                                }
                            )
                    } else {
                        // Empty state
                        VStack(spacing: 16) {
                            Circle()
                                .fill(Color(red: 0.87, green: 0.77, blue: 0.42).opacity(0.1))
                                .frame(width: 80, height: 80)
                                .overlay(
                                    Image(systemName: "plus")
                                        .font(.system(size: 32))
                                        .foregroundColor(Color(red: 0.87, green: 0.77, blue: 0.42))
                                )
                            
                            VStack(spacing: 4) {
                                Text("Tap to select video")
                                    .font(.system(size: 16, weight: .medium))
                                    .foregroundColor(.white)
                                
                                Text("Choose from library")
                                    .font(.system(size: 14))
                                    .foregroundColor(.gray)
                            }
                        }
                    }
                }
            )
            .onTapGesture {
                showingVideoPicker = true
            }
            .padding(16)
    }
    
    private var controlsSection: some View {
        VStack(spacing: 20) {
            // Location Toggle Card
            RoundedRectangle(cornerRadius: 16)
                .fill(Color(red: 0.16, green: 0.16, blue: 0.16))
                .frame(height: 72)
                .overlay(
                    HStack(spacing: 12) {
                        Circle()
                            .fill(Color(red: 0.87, green: 0.77, blue: 0.42).opacity(0.1))
                            .frame(width: 40, height: 40)
                            .overlay(
                                Image(systemName: "location.fill")
                                    .font(.system(size: 16))
                                    .foregroundColor(Color(red: 0.87, green: 0.77, blue: 0.42))
                            )
                        
                        VStack(alignment: .leading, spacing: 2) {
                            Text("Share to nearby")
                                .font(.system(size: 16, weight: .medium))
                                .foregroundColor(.white)
                            
                            Text("Let people nearby discover your video")
                                .font(.system(size: 14))
                                .foregroundColor(.gray)
                        }
                        
                        Spacer()
                        
                        Toggle("", isOn: $isLocationEnabled)
                            .toggleStyle(CustomToggleStyle())
                    }
                    .padding(16)
                )
            
            // Share Button
            Button(action: shareVideo) {
                HStack {
                    if viewModel.uploadState == .loading {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                            .scaleEffect(0.8)
                        
                        Text("Sharing...")
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.white)
                    } else {
                        Text("Share Video")
                            .font(.system(size: 18, weight: .bold))
                            .foregroundColor(videoData != nil ? .white : .gray)
                        
                        if videoData != nil {
                            Image(systemName: "arrow.right")
                                .font(.system(size: 16, weight: .medium))
                                .foregroundColor(.white)
                        }
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(height: 56)
                .background(
                    videoData != nil ?
                    Color(red: 0.87, green: 0.77, blue: 0.42) :
                    Color.gray.opacity(0.3)
                )
                .clipShape(RoundedRectangle(cornerRadius: 16))
            }
            .disabled(videoData == nil || viewModel.uploadState == .loading)
        }
        .padding(20)
        .background(Color(red: 0.1, green: 0.1, blue: 0.1))
    }
    
    private func loadVideo(from item: PhotosPickerItem?) {
        guard let item = item else { return }
        
        item.loadTransferable(type: VideoTransferable.self) { result in
            DispatchQueue.main.async {
                switch result {
                case .success(let video):
                    if let video = video {
                        self.videoURL = video.url
                        self.videoData = video.data
                        self.player = AVPlayer(url: video.url)
                    }
                case .failure(let error):
                    print("Error loading video: \(error)")
                }
            }
        }
    }
    
    private func shareVideo() {
        guard let videoData = videoData else { return }
        
        let geoLocation = isLocationEnabled ? locationManager.currentLocation : nil
        viewModel.shareVideo(data: videoData, geoLocation: geoLocation)
    }
    
    private func handleUploadState(_ state: UploadState) {
        switch state {
        case .idle:
            break
        case .loading:
            break
        case .success:
            dialogTitle = "Your video has been shared successfully"
            showingSuccessDialog = true
        case .error(let message):
            dialogTitle = message
            showingErrorDialog = true
        }
    }
}

// MARK: - Custom Toggle Style
struct CustomToggleStyle: ToggleStyle {
    func makeBody(configuration: Configuration) -> some View {
        HStack {
            configuration.label
            
            RoundedRectangle(cornerRadius: 16)
                .fill(configuration.isOn ?
                      Color(red: 0.87, green: 0.77, blue: 0.42).opacity(0.5) :
                      Color.gray.opacity(0.3))
                .frame(width: 50, height: 30)
                .overlay(
                    Circle()
                        .fill(configuration.isOn ?
                              Color(red: 0.87, green: 0.77, blue: 0.42) :
                              Color.gray)
                        .frame(width: 26, height: 26)
                        .offset(x: configuration.isOn ? 10 : -10)
                        .animation(.easeInOut(duration: 0.2), value: configuration.isOn)
                )
                .onTapGesture {
                    configuration.isOn.toggle()
                }
        }
    }
}

// MARK: - Video Transferable
struct VideoTransferable: Transferable {
    let url: URL
    let data: Data
    
    static var transferRepresentation: some TransferRepresentation {
        FileRepresentation(contentType: .movie) { video in
            SentTransferredFile(video.url)
        } importing: { received in
            let copy = URL.documentsDirectory.appending(path: "video_\(UUID().uuidString).mov")
            try FileManager.default.copyItem(at: received.file, to: copy)
            let data = try Data(contentsOf: copy)
            return VideoTransferable(url: copy, data: data)
        }
    }
}

// MARK: - Location Manager
class LocationManager: NSObject, ObservableObject, CLLocationManagerDelegate {
    private let manager = CLLocationManager()
    
    @Published var currentLocation: GeoLocationModel?
    @Published var locationError: String?
    
    override init() {
        super.init()
        manager.delegate = self
        manager.desiredAccuracy = kCLLocationAccuracyBest
    }
    
    func requestLocation() {
        guard manager.authorizationStatus == .authorizedWhenInUse ||
              manager.authorizationStatus == .authorizedAlways else {
            manager.requestWhenInUseAuthorization()
            return
        }
        
        manager.requestLocation()
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.first else { return }
        
        let geocoder = CLGeocoder()
        geocoder.reverseGeocodeLocation(location) { [weak self] placemarks, error in
            DispatchQueue.main.async {
                if let placemark = placemarks?.first {
                    self?.currentLocation = GeoLocationModel(
                        latitude: location.coordinate.latitude,
                        longitude: location.coordinate.longitude,
                        street: placemark.thoroughfare ?? "",
                        country: placemark.country ?? "",
                        postalCode: placemark.postalCode ?? "",
                        name: placemark.name ?? ""
                    )
                }
            }
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        DispatchQueue.main.async {
            self.locationError = "Unable to get your location"
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        if status == .denied || status == .restricted {
            DispatchQueue.main.async {
                self.locationError = "Please enable GPS to share location"
            }
        }
    }
}

// MARK: - View Model
class CreateShotViewModel: ObservableObject {
    @Published var uploadState: UploadState = .idle
    
    func shareVideo(data: Data, geoLocation: GeoLocationModel?) {
        uploadState = .loading
        
        // Simulate upload - replace with actual implementation
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
            // Simulate success/failure
            if Bool.random() {
                self.uploadState = .success
            } else {
                self.uploadState = .error("Upload failed. Please try again.")
            }
        }
    }
}

// MARK: - Upload State
enum UploadState: Equatable {
    case idle
    case loading
    case success
    case error(String)
}

// MARK: - Geo Location Model
struct GeoLocationModel {
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var street: String = ""
    var country: String = ""
    var postalCode: String = ""
    var name: String = ""
}
