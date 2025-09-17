import SwiftUI
import GoogleMaps
import ComposeApp


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
