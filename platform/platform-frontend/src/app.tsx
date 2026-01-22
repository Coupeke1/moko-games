import Auth from "@/components/global/auth";
import Notifications from "@/components/global/notifications";
import Scroller from "@/components/global/scroller";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import ChatPage from "@/features/chat/chat";
import CheckoutPage from "@/features/checkout/checkout";
import OrderPage from "@/features/checkout/order";
import FriendsPage from "@/features/friends/friends";
import RequestsPage from "@/features/friends/requests";
import LibraryGamePage from "@/features/library/game";
import LibraryPage from "@/features/library/library";
import GamePage from "@/features/lobby/components/game";
import LobbyPage from "@/features/lobby/lobby";
import NotFoundPage from "@/features/not-found";
import NotificationsPage from "@/features/notifications/notifications";
import { useMyProfile } from "@/features/profiles/hooks/use-my-profile";
import MyProfilePage from "@/features/profiles/my-profile";
import StoreGamePage from "@/features/store/game";
import StorePage from "@/features/store/store";
import { useAuthStore } from "@/stores/auth-store";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Navigate, Route, Routes } from "react-router";
import { Toaster } from "sonner";
import ProfilePage from "./features/profiles/profile";

const client = new QueryClient();

function App() {
    return (
        <QueryClientProvider client={client}>
            <BrowserRouter>
                <Auth />
                <Notifications />
                <Toaster
                    position="top-right"
                    toastOptions={{ className: "z-200" }}
                />
                <Content />
            </BrowserRouter>
        </QueryClientProvider>
    );
}

function Content() {
    const initialized = useAuthStore((state) => state.initialized);
    const { loading: profileLoading, error: profileError } = useMyProfile();

    if (!initialized || profileLoading) return <LoadingState />;
    if (profileError) return <ErrorState />;

    return (
        <>
            <Scroller />

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

                <Route path="/profile" element={<MyProfilePage />} />
                <Route path="/profiles/:name" element={<ProfilePage />} />

                <Route path="/friends" element={<FriendsPage />} />
                <Route path="/friends/requests" element={<RequestsPage />} />

                <Route path="/notifications" element={<NotificationsPage />} />

                <Route path="/chat" element={<ChatPage />} />

                <Route path="*" element={<NotFoundPage />} />
            </Routes>
        </>
    );
}

export default App;
