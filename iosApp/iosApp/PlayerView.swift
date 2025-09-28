//
//  PlayerView.swift
//  iosApp
//
//  Created by Aissam EL BOUDI on 27/9/2025.
//  Copyright © 2025 orgName. All rights reserved.
//
import SwiftUI
import AVFoundation
import AVKit
import Combine
import ComposeApp

// MARK: - Data Models
struct ShotModel: Codable, Identifiable {
    let id: String
    let mediaUrl: String?
    let profile: Profile
    
    struct Profile: Codable {
        let photoUrl: String?
        let username: String?
    }
}

// MARK: - Video Player Item
class VideoPlayerItem: ObservableObject {
    let player: AVPlayer
    var pageIndex: Int
    @Published var isReady: Bool = false
    @Published var isPlaying: Bool = false
    @Published var isBuffering: Bool = false
    @Published var hasError: Bool = false
    var videoUrl: String = ""
    private var observers: [Any] = []
    
    init(pageIndex: Int = -1) {
        self.player = AVPlayer()
        self.pageIndex = pageIndex
        setupPlayer()
    }
    
    private func setupPlayer() {
        // Configure audio session for background playback
        do {
            try AVAudioSession.sharedInstance().setCategory(.playback, mode: .moviePlayback)
            try AVAudioSession.sharedInstance().setActive(true)
        } catch {
            print("Audio session setup failed: \(error)")
        }
        
        // Add observers
        let timeObserver = player.addPeriodicTimeObserver(
            forInterval: CMTime(seconds: 0.1, preferredTimescale: 600),
            queue: .main
        ) { [weak self] _ in
            // Update playing state
            self?.isPlaying = self?.player.rate != 0
        }
        observers.append(timeObserver)
        
        // Player status observer
        let statusObserver = player.observe(\.status, options: [.new]) { [weak self] player, _ in
            DispatchQueue.main.async {
                switch player.status {
                case .readyToPlay:
                    self?.isReady = true
                    self?.isBuffering = false
                    self?.hasError = false
                case .failed:
                    self?.hasError = true
                    self?.isBuffering = false
                    self?.isReady = false
                default:
                    break
                }
            }
        }
        observers.append(statusObserver)
        
        // Time control status observer for buffering
        let bufferObserver = player.observe(\.timeControlStatus, options: [.new]) { [weak self] player, _ in
            DispatchQueue.main.async {
                switch player.timeControlStatus {
                case .playing:
                    self?.isPlaying = true
                    self?.isBuffering = false
                case .paused:
                    self?.isPlaying = false
                    self?.isBuffering = false
                case .waitingToPlayAtSpecifiedRate:
                    self?.isBuffering = true
                @unknown default:
                    break
                }
            }
        }
        observers.append(bufferObserver)
        
        // Playback end observer
        NotificationCenter.default.addObserver(
            forName: .AVPlayerItemDidPlayToEndTime,
            object: nil,
            queue: .main
        ) { [weak self] _ in
            self?.player.seek(to: .zero)
            self?.player.play()
        }
    }
    
    func setVideo(url: String) {
        guard !url.isEmpty, let videoURL = URL(string: url) else {
            hasError = true
            return
        }
        
        videoUrl = url
        isReady = false
        hasError = false
        isBuffering = true
        
        let playerItem = AVPlayerItem(url: videoURL)
        player.replaceCurrentItem(with: playerItem)
    }
    
    func play() {
        guard isReady && !hasError else { return }
        player.play()
    }
    
    func pause() {
        player.pause()
    }
    
    func stop() {
        player.pause()
        player.seek(to: .zero)
    }
    
    deinit {
        observers.forEach { observer in
            if let timeObserver = observer as? Any {
                player.removeTimeObserver(timeObserver)
            }
        }
        NotificationCenter.default.removeObserver(self)
    }
}

// MARK: - Player State Enum
enum PlayerState {
    case idle
    case loading
    case ready
    case playing
    case buffering
    case error
}

// MARK: - Main Player View
struct PlayerView: View {
    private let viewModel: MomentsViewModel // Injected by Koin
    @State private var items: [ShotModel] = []
    @State private var debugInfo = "Loading..."
    @State private var isLoading = false
    @State private var playerStates: [Int: PlayerState] = [:]
    @State private var currentPage = 0
    
    // Player pool - keeping 5 players as in Android version
    @State private var playerPool: [VideoPlayerItem] = []
    @State private var pageToPlayerMap: [Int: VideoPlayerItem] = [:]
    
    init() {
        viewModel = ViewModelProvider().getMomentsViewModel()
    }
    
    var body: some View {
        ZStack {
            Color.black.ignoresSafeArea()
            
            if items.isEmpty && isLoading {
                loadingView
            } else if items.isEmpty && !isLoading {
                errorView
            } else {
                verticalPagerView
            }
        }
        .onAppear {
            setupPlayerPool()
            observeViewModel() // Observe ViewModel StateFlow
            //viewModel.getShots() // Trigger initial load
        }
        .onDisappear {
            cleanupPlayers()
        }
    }
    
    private var loadingView: some View {
        VStack(spacing: 16) {
            ProgressView()
                .progressViewStyle(CircularProgressViewStyle(tint: .gray))
                .scaleEffect(0.8)
            
            Text(debugInfo)
                .foregroundColor(.white)
                .font(.system(size: 16))
        }
    }
    
    private var errorView: some View {
        VStack(spacing: 16) {
            Text(debugInfo)
                .foregroundColor(.red)
                .font(.system(size: 16))
            
            Button("Retry") {
                //viewModel.getShots() // Use injected viewModel
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 10)
            .background(Color(red: 0.87, green: 0.77, blue: 0.42))
            .foregroundColor(.black)
            .cornerRadius(8)
        }
    }
    
    private var verticalPagerView: some View {
        TabView(selection: $currentPage) {
            ForEach(0..<items.count, id: \.self) { page in
                videoPlayerView(for: page)
                    .tag(page)
            }
        }
        .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
        .indexViewStyle(PageIndexViewStyle(backgroundDisplayMode: .never))
        .onChange(of: currentPage) { _, newPage in
            handlePageChange(newPage)
        }
        .onChange(of: items.count) { _, count in
            if count > 0 && pageToPlayerMap.isEmpty {
                assignPlayersToPages(centerPage: 0)
            }
            
            // Load more content when approaching the end
            if count > 0 && currentPage >= count - 2 && !isLoading {
                //viewModel.getShots() // Use injected viewModel
            }
        }
    }
    
    private func videoPlayerView(for page: Int) -> some View {
        ZStack {
            Color.black.ignoresSafeArea()
            
            if let playerItem = pageToPlayerMap[page],
               let shot = items[safe: page] {
                
                VideoPlayer(player: playerItem.player)
                    .disabled(true)
                    .onAppear {
                        if playerItem.isReady && !playerItem.hasError {
                            playerItem.play()
                        }
                    }
                
                // Player state overlays
                playerStateOverlay(for: page, playerItem: playerItem)
                
                // Social overlay - only show when ready or playing
                let state = playerStates[page] ?? .idle
                if state == .ready || state == .playing {
                    SocialOverlay(shot: shot)
                }
            } else {
                // Loading state for pages without assigned players
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: .gray))
            }
        }
    }
    
    private func playerStateOverlay(for page: Int, playerItem: VideoPlayerItem) -> some View {
        Group {
            switch playerStates[page] ?? .idle {
            case .loading:
                VStack {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .gray))
                        .frame(width: 50, height: 50)
                }
                
            case .error:
                VStack(spacing: 16) {
                    Image(systemName: "xmark.circle")
                        .font(.system(size: 48))
                        .foregroundColor(.red)
                    
                    Text("Video unavailable")
                        .foregroundColor(.white)
                        .font(.system(size: 16))
                    
                    Button("Retry") {
                        assignPlayersToPages(centerPage: currentPage)
                    }
                    .padding(.horizontal, 20)
                    .padding(.vertical, 10)
                    .background(Color(red: 0.87, green: 0.77, blue: 0.42))
                    .foregroundColor(.black)
                    .cornerRadius(8)
                }
                
            case .buffering:
                VStack {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .gray))
                        .frame(width: 30, height: 30)
                }
                
            default:
                EmptyView()
            }
        }
    }
    
    // MARK: - Helper Methods
    
    private func setupPlayerPool() {
        playerPool = (0..<5).map { _ in VideoPlayerItem() }
    }
    
    // MARK: - ViewModel Integration
    private func observeViewModel() {
        // In a real KMP setup, you would observe the StateFlow from the ViewModel
        // This is a placeholder showing how you'd integrate with the actual ViewModel
        
        // Example of how you might observe the StateFlow in SwiftUI:
        // viewModel.getShotsState.collect { state in
        //     DispatchQueue.main.async {
        //         switch state {
        //         case is ResponseState.Success:
        //             let successState = state as! ResponseState.Success
        //             if !successState.data.isEmpty {
        //                 debugInfo = "SUCCESS: Got \(successState.data.count) videos"
        //                 items.append(contentsOf: successState.data)
        //                 isLoading = false
        //             } else {
        //                 debugInfo = "No more videos available"
        //                 isLoading = false
        //             }
        //         case is ResponseState.Loading:
        //             debugInfo = "LOADING..."
        //             isLoading = true
        //         case is ResponseState.Error:
        //             let errorState = state as! ResponseState.Error
        //             debugInfo = "ERROR: \(errorState.error.localizedDescription)"
        //             isLoading = false
        //         default:
        //             debugInfo = "IDLE"
        //         }
        //     }
        // }
        
        // For now, using mock data - replace with actual StateFlow observation
        print("lklkljhghghgfdfd")
        loadMockData()
    }
    
    private func loadMockData() {
        isLoading = true
        debugInfo = "Loading..."
        
        // This simulates the API response - replace with actual StateFlow observation
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            print("lklkljhghghgfdfd 222222")

            let newShots = generateMockShots(count: 10)
            items.append(contentsOf: newShots)
            isLoading = false
            debugInfo = "SUCCESS: Got \(newShots.count) videos"
        }
    }
    
    private func generateMockShots(count: Int) -> [ShotModel] {
        let videoUrls = [
            "https://nearvibe.fra1.digitaloceanspaces.com/e0295157-9e49-4d3d-9716-505b20e1c02f",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
        ]
        
        return (0..<count).map { index in
            ShotModel(
                id: UUID().uuidString,
                mediaUrl: videoUrls[index % videoUrls.count],
                profile: ShotModel.Profile(
                    photoUrl: "https://via.placeholder.com/56",
                    username: "user\(index)"
                )
            )
        }
    }
    
    private func assignPlayersToPages(centerPage: Int) {
        let targetPages = [
            centerPage - 1,
            centerPage,
            centerPage + 1
        ].filter { $0 >= 0 && $0 < items.count }
        
        print("🎯 Target pages: \(targetPages) for center: \(centerPage)")
        
        var newMapping = pageToPlayerMap
        var newStates = playerStates
        
        // Keep existing players that are still needed
        let pagesToKeep = targetPages.filter { newMapping[$0] != nil }
        print("♻️ Keeping players for pages: \(pagesToKeep)")
        
        // Find pages that need new players
        let pagesNeedingPlayers = targetPages.filter { newMapping[$0] == nil }
        
        // Find available players
        let availablePlayers = playerPool.filter { player in
            !newMapping.values.contains { $0 === player }
        }
        
        // Assign available players
        for (index, page) in pagesNeedingPlayers.enumerated() {
            guard index < availablePlayers.count,
                  let shot = items[safe: page] else { continue }
            
            let playerItem = availablePlayers[index]
            let videoUrl = shot.mediaUrl ?? ""
            
            print("🔄 Assigning player to page \(page): \(videoUrl)")
            newStates[page] = .loading
            
            // Reset player
            playerItem.stop()
            playerItem.hasError = false
            playerItem.isReady = false
            playerItem.pageIndex = page
            
            // Set new video
            playerItem.setVideo(url: videoUrl)
            newMapping[page] = playerItem
            
            // Monitor player state changes
            monitorPlayerState(playerItem: playerItem, page: page) { state in
                DispatchQueue.main.async {
                    playerStates[page] = state
                    
                    // Auto-play if this is the current page and ready
                    if page == centerPage && state == .ready {
                        playerItem.play()
                        playerStates[page] = .playing
                    }
                }
            }
        }
        
        pageToPlayerMap = newMapping
        playerStates = newStates
    }
    
    private func monitorPlayerState(playerItem: VideoPlayerItem, page: Int, completion: @escaping (PlayerState) -> Void) {
        // Create a monitoring task
        Timer.scheduledTimer(withTimeInterval: 0.1, repeats: true) { timer in
            let state: PlayerState
            
            if playerItem.hasError {
                state = .error
                timer.invalidate()
            } else if playerItem.isBuffering {
                state = .buffering
            } else if playerItem.isReady {
                if playerItem.isPlaying {
                    state = .playing
                } else {
                    state = .ready
                }
                timer.invalidate()
            } else {
                state = .loading
            }
            
            completion(state)
            
            if state == .error || state == .ready || state == .playing {
                timer.invalidate()
            }
        }
    }
    
    private func handlePageChange(_ newPage: Int) {
        print("📄 Page changed to: \(newPage)")
        
        // Stop all players
        playerPool.forEach { $0.pause() }
        
        // Play current page if ready
        if let playerItem = pageToPlayerMap[newPage] {
            if playerItem.isReady && !playerItem.hasError {
                playerItem.play()
                playerStates[newPage] = .playing
                print("▶️ Playing page \(newPage) (ready)")
            }
        }
        
        // Reassign players for new position
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
            assignPlayersToPages(centerPage: newPage)
        }
    }
    
    private func cleanupPlayers() {
        playerPool.forEach { playerItem in
            playerItem.pause()
        }
    }
}

// MARK: - Social Overlay
struct SocialOverlay: View {
    let shot: ShotModel
    @State private var likes = Int.random(in: 100...50000)
    @State private var comments = Int.random(in: 10...1000)
    @State private var views = Int.random(in: 1000...100000)
    @State private var isLiked = false
    @State private var isFollowing = false
    
    var body: some View {
        ZStack {
            // Top gradient
            LinearGradient(
                colors: [Color.black.opacity(0.6), Color.clear],
                startPoint: .top,
                endPoint: .bottom
            )
            .frame(height: 100)
            .frame(maxHeight: .infinity, alignment: .top)
            
            // Bottom gradient
            LinearGradient(
                colors: [Color.clear, Color.black.opacity(0.8)],
                startPoint: .top,
                endPoint: .bottom
            )
            .frame(height: 200)
            .frame(maxHeight: .infinity, alignment: .bottom)
            
            HStack {
                // Left side content
                VStack(alignment: .leading, spacing: 0) {
                    Spacer()
                    
                    // Username
                    Text("Aissam elboudi")
                        .foregroundColor(.white)
                        .font(.system(size: 16, weight: .bold))
                    
                    // Caption
                    Text("Amazing video! Check this out 🔥")
                        .foregroundColor(.white)
                        .font(.system(size: 14))
                        .lineLimit(3)
                        .padding(.top, 8)
                    
                    // Hashtags
                    HStack(spacing: 8) {
                        ForEach(["#fyp", "#viral", "#trending"], id: \.self) { hashtag in
                            Text(hashtag)
                                .foregroundColor(Color(red: 0.87, green: 0.77, blue: 0.42))
                                .font(.system(size: 12, weight: .medium))
                                .padding(.horizontal, 8)
                                .padding(.vertical, 4)
                                .background(Color.black.opacity(0.3))
                                .cornerRadius(12)
                        }
                    }
                    .padding(.top, 12)
                    
                    // Music info
                    HStack(spacing: 8) {
                        Image(systemName: "music.note")
                            .foregroundColor(.white)
                            .font(.system(size: 16))
                        
                        Text("Original Audio")
                            .foregroundColor(.white)
                            .font(.system(size: 12))
                            .lineLimit(1)
                    }
                    .padding(8)
                    .background(Color.black.opacity(0.4))
                    .cornerRadius(20)
                    .padding(.top, 16)
                    .padding(.bottom, 16)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.leading, 16)
                
                // Right side actions
                VStack(spacing: 24) {
                    // Profile picture
                    AsyncImage(url: URL(string: shot.profile.photoUrl ?? "")) { image in
                        image
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                    } placeholder: {
                        Circle()
                            .fill(Color.gray)
                    }
                    .frame(width: 56, height: 56)
                    .clipShape(Circle())
                    .overlay(Circle().stroke(Color.white, lineWidth: 2))
                    
                    // Like button
                    SocialActionButton(
                        systemImage: isLiked ? "heart.fill" : "heart",
                        count: isLiked ? likes + 1 : likes,
                        color: isLiked ? .red : .white
                    ) {
                        isLiked.toggle()
                    }
                    
                    // Comment button
                    SocialActionButton(
                        systemImage: "bubble.right",
                        count: comments,
                        color: .white
                    ) {
                        // Handle comment
                    }
                    
                    // Share button
                    SocialActionButton(
                        systemImage: "square.and.arrow.up",
                        count: nil,
                        color: .white
                    ) {
                        // Handle share
                    }
                    
                    // Views
                    VStack(spacing: 4) {
                        Image(systemName: "play.fill")
                            .foregroundColor(.white)
                            .font(.system(size: 32))
                            .background(
                                Circle()
                                    .fill(Color.black.opacity(0.3))
                                    .frame(width: 40, height: 40)
                            )
                        
                        Text(formatCount(views))
                            .foregroundColor(.white)
                            .font(.system(size: 12, weight: .medium))
                    }
                }
                .padding(.trailing, 16)
                .frame(maxHeight: .infinity, alignment: .center)
            }
        }
    }
}

// MARK: - Social Action Button
struct SocialActionButton: View {
    let systemImage: String
    let count: Int?
    let color: Color
    let action: () -> Void
    
    var body: some View {
        VStack(spacing: 4) {
            Button(action: action) {
                Image(systemName: systemImage)
                    .foregroundColor(color)
                    .font(.system(size: 22))
                    .frame(width: 40, height: 40)
                    .background(
                        Circle()
                            .fill(Color.black.opacity(0.3))
                    )
            }
            
            if let count = count {
                Text(formatCount(count))
                    .foregroundColor(.white)
                    .font(.system(size: 12, weight: .medium))
            }
        }
    }
}

// MARK: - Extensions
extension Array {
    subscript(safe index: Index) -> Element? {
        return indices.contains(index) ? self[index] : nil
    }
}

// MARK: - Helper Functions
func formatCount(_ count: Int) -> String {
    switch count {
    case 1_000_000...:
        return String(format: "%.1fM", Double(count) / 1_000_000)
    case 1_000...:
        return String(format: "%.1fK", Double(count) / 1_000)
    default:
        return "\(count)"
    }
}

// MARK: - Mock ViewModel (Remove this when using actual KMP ViewModel)
class ShotsViewModel: ObservableObject {
    // This is just a placeholder - your actual ViewModel from KMP will be injected
    // The actual ViewModel should have:
    // - getShotsState: StateFlow<ResponseState<List<ShotModel>>>
    // - getShots() method
    
    func getShots() {
        // This will be handled by your actual KMP ViewModel
        print("getShots() called on ViewModel")
    }
}
