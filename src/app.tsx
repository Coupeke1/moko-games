import LibraryPage from "@/routes/library/library"
import ProfilePage from "@/routes/profile/profile"
import StorePage from "@/routes/store/store"
import { BrowserRouter, Navigate, Route, Routes } from "react-router"

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Navigate to="/store" />} />
                <Route path="/store" element={<StorePage />} />
                <Route path="/library" element={<LibraryPage />} />
                <Route path="/profile" element={<ProfilePage />} />
            </Routes>
        </BrowserRouter>
    )
}

export default App
