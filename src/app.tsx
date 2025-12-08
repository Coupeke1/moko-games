import Auth from "@/components/auth";
import LoadingState from "@/components/state/loading";
import FriendsPage from "@/routes/friends/friends";
import IncomingRequestsPage from "@/routes/friends/incoming";
import OutgoingRequestsPage from "@/routes/friends/outgoing";
import LibraryPage from "@/routes/library/library";
import LobbyPage from "@/routes/lobby/lobby";
import ProfilePage from "@/routes/profile/profile";
import StorePage from "@/routes/store/store";
import { useAuthStore } from "@/stores/auth-store";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Navigate, Route, Routes } from "react-router";
import { Toaster } from "sonner";
import LibraryGamePage from "@/routes/library/game";
import StoreGamePage from "@/routes/store/game";

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
                        <Route path="/store/:id" element={<StoreGamePage />} />

                        <Route path="/library" element={<LibraryPage />} />
                        <Route
                            path="/library/:id"
                            element={<LibraryGamePage />}
                        />

                        <Route path="/lobby/:id" element={<LobbyPage />} />

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
                    </Routes>
                )}

                <Toaster
                    position="top-right"
                    toastOptions={{ className: "z-200" }}
                />
            </BrowserRouter>
        </QueryClientProvider>
    );
}

export default App;
