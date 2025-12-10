import {BrowserRouter, Route, Routes} from "react-router"
import {QueryClient, QueryClientProvider} from "@tanstack/react-query"
import Auth from "@/components/auth.tsx"
import LoadingState from "@/components/state/loading.tsx";
import {useAuthStore} from "@/stores/auth-store.ts";
import GamePage from "./routes/game";
import {Toaster} from "sonner";

const client = new QueryClient();

function App() {
    const initialized = useAuthStore((state) => state.initialized);
    const token = useAuthStore((state) => state.token);

    return (
        <QueryClientProvider client={client}>
            <BrowserRouter basename="/checkers">
                <Auth/>

                {!initialized || !token ? (
                    <LoadingState/>
                ) : (
                    <Routes>
                        <Route path="/game/:id" element={<GamePage/>}/>
                        <Route path="/" element={<GamePage/>}/>
                    </Routes>
                )}
                <Toaster
                    position="top-right"
                    toastOptions={{className: "z-200"}}
                />
            </BrowserRouter>
        </QueryClientProvider>
    )
}

export default App;