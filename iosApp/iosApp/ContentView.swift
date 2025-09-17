import UIKit
import SwiftUI
import ComposeApp
import GoogleMaps
import GoogleMapsUtils

struct GoogleMapView: UIViewRepresentable {
    func makeUIView(context: Context) -> GMSMapView {
        let options = GMSMapViewOptions()
        options.camera = GMSCameraPosition.camera(withLatitude: 12.952636, longitude: 77.653059, zoom: 10.0)

        let mapView = GMSMapView(options: options)

        // Creates a marker in the center of the map.
       
        
        // Add heatmap
        addHeatmap(to: mapView)
        
        return mapView
    }

    func updateUIView(_ uiView: GMSMapView, context: Context) {}
    
    private func addHeatmap(to mapView: GMSMapView) {
        // Sample data points for heatmap (you can replace with your actual data)
        let heatmapData: [GMUWeightedLatLng] = [
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 12.952636, longitude: 77.653059), intensity: 1.0),
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 12.954, longitude: 77.655), intensity: 0.8),
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 12.950, longitude: 77.650), intensity: 0.9),
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 12.955, longitude: 77.658), intensity: 0.7),
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 12.948, longitude: 77.652), intensity: 0.6),
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 12.957, longitude: 77.660), intensity: 0.8),
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 12.949, longitude: 77.648), intensity: 0.5),
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 12.956, longitude: 77.656), intensity: 0.9),
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 12.951, longitude: 77.654), intensity: 0.7),
            GMUWeightedLatLng(coordinate: CLLocationCoordinate2D(latitude: 12.953, longitude: 77.651), intensity: 0.8)
        ]
        
        // Create heatmap layer
        let heatmapLayer = GMUHeatmapTileLayer()
        heatmapLayer.weightedData = heatmapData
        
        // Customize heatmap appearance (optional)
        heatmapLayer.radius = 80 // Radius of influence for each point
        heatmapLayer.opacity = 0.7 // Transparency of the heatmap
        
        // Optional: Customize gradient colors
        let gradientColors: [UIColor] = [
            .clear,
            .blue,
            .cyan,
            .green,
            .yellow,
            .orange,
            .red
        ]
        let gradientStartPoints: [NSNumber] = [0.0, 0.1, 0.2, 0.4, 0.6, 0.8, 1.0]
        
        heatmapLayer.gradient = GMUGradient(
            colors: gradientColors,
            startPoints: gradientStartPoints,
            colorMapSize: 256
        )
        
        // Add heatmap to map
        heatmapLayer.map = mapView
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(
            mapUIViewController: { () -> UIViewController in
                return UIHostingController(rootView: GoogleMapView())
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}
