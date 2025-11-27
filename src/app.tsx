import GamePage from "@/routes/game/game.tsx"
import { BrowserRouter, Route, Routes } from "react-router"
import Auth from "@/components/auth.tsx";
import { useAuthStore } from "./stores/auth-store";
import LoadingState from "./components/state/loading";

function App() {
    const initialized = useAuthStore(state => state.initialized);
    const token = useAuthStore(state => state.token);

    return (
        <BrowserRouter>
            <Auth />
            {!initialized || !token ? (
                <LoadingState />
            ) : (
                <Routes>
                    <Route path="/game/:id" element={<GamePage />} />
                </Routes>
            )}
        </BrowserRouter>
    )
}

export default App;