import Auth from "@/components/auth";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import CheckoutPage from "@/features/checkout/checkout";
import FriendsPage from "@/features/friends/friends";
import IncomingRequestsPage from "@/features/friends/incoming";
import OutgoingRequestsPage from "@/features/friends/outgoing";
import GamePage from "@/features/game/game";
import InvitesPage from "@/features/invites/invites";
import LibraryGamePage from "@/features/library/game";
import LibraryPage from "@/features/library/library";
import LobbyPage from "@/features/lobby/lobby";
import { useProfile } from "@/features/profile/hooks/use-profile";
import ProfilePage from "@/features/profile/profile";
import StoreGamePage from "@/features/store/game";
import StorePage from "@/features/store/store";
import { useAuthStore } from "@/stores/auth-store";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Navigate, Route, Routes } from "react-router";
import { Toaster } from "sonner";
import OrderPage from "@/features/checkout/order";

const client = new QueryClient();

function App() {
    return (
        <QueryClientProvider client={client}>
            <Content />
        </QueryClientProvider>
    );
}

function Content() {
    const initialized = useAuthStore((state) => state.initialized);
    const { loading: profileLoading, error: profileError } = useProfile();

    if (!initialized || profileLoading) {
        return (
            <BrowserRouter>
                <Auth />
                <LoadingState />
            </BrowserRouter>
        );
    }

    if (profileError) {
        return (
            <BrowserRouter>
                <Auth />
                <ErrorState />
            </BrowserRouter>
        );
    }

    return (
        <BrowserRouter>
            <Auth />

            <Routes>
                <Route path="/" element={<Navigate to="/store" />} />
                <Route path="/store" element={<StorePage />} />
                <Route path="/store/:id" element={<StoreGamePage />} />

                <Route path="/store/checkout" element={<CheckoutPage />} />
                <Route path="/store/checkout/:id" element={<OrderPage />} />

                <Route path="/library" element={<LibraryPage />} />
                <Route path="/library/:id" element={<LibraryGamePage />} />

                <Route path="/lobbies/:id" element={<LobbyPage />} />
                <Route path="/lobbies/:id/game" element={<GamePage />} />
                <Route path="/invites/:id" element={<InvitesPage />} />

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

            <Toaster
                position="top-right"
                toastOptions={{ className: "z-200" }}
            />
        </BrowserRouter>
    );
}

export default App;
