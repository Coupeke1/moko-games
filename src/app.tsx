import GamePage from "@/routes/game/game"
import {BrowserRouter, Route, Routes} from "react-router"
import {QueryClient, QueryClientProvider} from "@tanstack/react-query"
import Auth from "@/components/auth.tsx"
import LoadingState from "@/components/state/loading.tsx";
import {useAuthStore} from "@/stores/auth-store.ts";

const queryClient = new QueryClient();

function App() {
    const initialized = useAuthStore((state) => state.initialized);
    const token = useAuthStore((state) => state.token);

    return (
        <QueryClientProvider client={queryClient}>
            <BrowserRouter basename="/checkers">
                <Auth />

                {!initialized || !token ? (
                    <LoadingState />
                ) : (
                    <Routes>
                        <Route path="/game/:gameId" element={<GamePage/>}/>
                        <Route path="/" element={<GamePage/>}/>
                    </Routes>
                )}
            </BrowserRouter>
        </QueryClientProvider>
    )
}

export default App;