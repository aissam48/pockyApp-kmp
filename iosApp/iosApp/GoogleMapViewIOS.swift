import SwiftUI
import GoogleMaps
import shared
import fondation

struct GoogleMapViewIOS: UIViewRepresentable {
    func makeUIView(context: Context) -> GMSMapView {
        let camera = GMSCameraPosition.camera(withLatitude: 37.7749,
                                              longitude: -122.4194,
                                              zoom: 12)
        let mapView = GMSMapView(frame: .zero, camera: camera)

        let marker = GMSMarker()
        marker.position = CLLocationCoordinate2D(latitude: 37.7749, longitude: -122.4194)
        marker.title = "San Francisco"
        marker.map = mapView

        return mapView
    }

    func updateUIView(_ uiView: GMSMapView, context: Context) {}
}
