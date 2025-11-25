import Auth from "@/components/auth"
import CartPage from "@/routes/cart/cart"
import ChatPage from "@/routes/chat/chat"
import LibraryPage from "@/routes/library/library"
import NotificationsPage from "@/routes/notifications/notifications"
import ProfilePage from "@/routes/profile/profile"
import StorePage from "@/routes/store/store"
import { QueryClient, QueryClientProvider } from "@tanstack/react-query"
import { BrowserRouter, Navigate, Route, Routes } from "react-router"

const client = new QueryClient();

function App() {
    return (
        <QueryClientProvider client={client}>
            <BrowserRouter>
                <Auth />

                <Routes>
                    <Route path="/" element={<Navigate to="/store" />} />

                    <Route path="/store" element={<StorePage />} />
                    <Route path="/library" element={<LibraryPage />} />
                    <Route path="/profile" element={<ProfilePage />} />

                    <Route path="/notifications" element={<NotificationsPage />} />
                    <Route path="/chat" element={<ChatPage />} />
                    <Route path="/cart" element={<CartPage />} />
                </Routes>
            </BrowserRouter>
        </QueryClientProvider>
    )
}

export default App;