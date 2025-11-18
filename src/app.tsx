import CartPage from "@/routes/cart/cart"
import ChatPage from "@/routes/chat/chat"
import FriendsPage from "@/routes/friends/friends"
import LibraryPage from "@/routes/library/library"
import NotificationsPage from "@/routes/notifications/notifications"
import ProfilePage from "@/routes/profile/profile"
import StorePage from "@/routes/store/store"
import { BrowserRouter, Navigate, Route, Routes } from "react-router"

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Navigate to="/store" />} />

                <Route path="/store" element={<StorePage />} />
                <Route path="/library" element={<LibraryPage />} />
                <Route path="/profile" element={<ProfilePage />} />

                <Route path="/notifications" element={<NotificationsPage />} />
                <Route path="/friends" element={<FriendsPage />} />
                <Route path="/chat" element={<ChatPage />} />
                <Route path="/cart" element={<CartPage />} />
            </Routes>
        </BrowserRouter>
    )
}

export default App
