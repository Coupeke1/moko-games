import { Link as RouterLink, useLocation } from "react-router";


interface Props {
    title: string;
    path: string;
}

export default function TabLink({ title, path }: Props) {
    const location = useLocation();
    const matches: boolean = location.pathname === path;

    return (
        <RouterLink to={path} className={`text-lg text-center cursor-pointer font-semibold transition-colors duration-75 px-2.5 py-0.5 rounded-lg min-w-32 ${matches ? "bg-bg-2" : " hover:bg-bg-2"}`}>
            <p>{title}</p>
        </RouterLink>
    )
}