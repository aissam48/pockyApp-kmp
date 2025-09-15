import SwiftUI


import SwiftUI
import GoogleMaps


@main
struct iOSApp: App {

    init() {
        GMSServices.provideAPIKey("AIzaSyCr_Z1knx-JMf60wL9r_XGrzPNobILp1fI")
        // Enable Metal renderer for better performance
        GMSServices.setMetalRendererEnabled(true)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
