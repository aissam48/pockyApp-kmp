import SwiftUI


import SwiftUI
import GoogleMaps


@main
struct iOSApp: App {

    init() {
        GMSServices.provideAPIKey("AIzaSyCr_Z1knx-JMf60wL9r_XGrzPNobILp1fI")
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
