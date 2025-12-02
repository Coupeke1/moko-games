import { useAuthStore } from "@/stores/auth-store";
import GamePage from "@/routes/game"
import LoadingState from "@/components/state/loading";
import Auth from "@/components/auth.tsx";
import { BrowserRouter, Route, Routes } from "react-router"
import { Toaster } from "sonner";

function App() {
    const initialized = useAuthStore(state => state.initialized);
    const token = useAuthStore(state => state.token);

    return (
        <BrowserRouter basename="/tic-tac-toe">
            <Auth />
            {!initialized || !token ? (
                <LoadingState />
            ) : (
                <Routes>
                    <Route path="/game/:id" element={<GamePage />} />
                </Routes>
            )}

            <Toaster position="bottom-center" />
        </BrowserRouter>
    )
}

export default App;