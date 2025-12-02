interface Props {
    className?: string
}

export default function Cross({className = 'size-12'}: Props) {
    return (
        <svg
            xmlns='http://www.w3.org/2000/svg'
            xmlSpace='preserve'
            fillRule='evenodd'
            strokeLinejoin='round'
            strokeMiterlimit='2'
            clipRule='evenodd'
            viewBox='0 0 240 240'
            className={className}
        >
            <path
                fill='#fff'
                d='m189.492 15.492 35.017 35.017-69.492 69.492 69.492 69.492-35.017 35.017L120 155.018 50.508 224.51l-35.017-35.017 69.492-69.492-69.492-69.492 35.017-35.017L120 84.984z'
            ></path>
        </svg>
    );
}