import Auth from "@/components/auth";
import LoadingState from "@/components/state/loading";
import CartPage from "@/routes/cart/cart";
import ChatPage from "@/routes/chat/chat";
import FriendsPage from "@/routes/friends/friends";
import IncomingRequestsPage from "@/routes/friends/incoming";
import OutgoingRequestsPage from "@/routes/friends/outgoing";
import LibraryPage from "@/routes/library/library";
import LobbyPlayersPage from "@/routes/lobby/players";
import LobbySettingsPage from "@/routes/lobby/settings";
import NotificationsPage from "@/routes/notifications/notifications";
import ProfilePage from "@/routes/profile/profile";
import StorePage from "@/routes/store/store";
import { useAuthStore } from "@/stores/auth-store";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Navigate, Route, Routes } from "react-router";
import { Toaster } from "sonner";

const client = new QueryClient();

function App() {
    const initialized = useAuthStore((state) => state.initialized);
    const token = useAuthStore((state) => state.token);

    return (
        <QueryClientProvider client={client}>
            <BrowserRouter>
                <Auth />

                {!initialized || !token ? (
                    <LoadingState />
                ) : (
                    <Routes>
                        <Route path="/" element={<Navigate to="/store" />} />
                        <Route path="/store" element={<StorePage />} />
                        <Route path="/library" element={<LibraryPage />} />
                        <Route
                            path="/lobby/:id/players"
                            element={<LobbyPlayersPage />}
                        />
                        <Route
                            path="/lobby/:id/settings"
                            element={<LobbySettingsPage />}
                        />
                        <Route path="/profile" element={<ProfilePage />} />
                        <Route path="/friends" element={<FriendsPage />} />
                        <Route
                            path="/friends/requests/incoming"
                            element={<IncomingRequestsPage />}
                        />
                        <Route
                            path="/friends/requests/outgoing"
                            element={<OutgoingRequestsPage />}
                        />
                        <Route
                            path="/notifications"
                            element={<NotificationsPage />}
                        />
                        <Route path="/chat" element={<ChatPage />} />
                        <Route path="/cart" element={<CartPage />} />
                    </Routes>
                )}

                <Toaster position="top-right" />
            </BrowserRouter>
        </QueryClientProvider>
    );
}

export default App;
