import GamePage from "@/routes/game/game"
import { BrowserRouter, Route, Routes } from "react-router"

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<GamePage />} />
            </Routes>
        </BrowserRouter>
    )
}

export default App;