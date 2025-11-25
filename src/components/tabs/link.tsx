interface Props {
    title: string;
    active: boolean;
    onClick: (title: string) => void;
}

export default function TabLink({ title, active, onClick }: Props) {
    return (
        <button onClick={() => onClick(title)} className={`text-lg cursor-pointer font-semibold transition-colors duration-75 px-2.5 py-0.5 rounded-lg min-w-24 ${active ? "bg-bg-2" : " hover:bg-bg-2"}`}>
            <p>{title}</p>
        </button>
    )
}