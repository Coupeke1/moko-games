interface Props {
    className?: string
}

export default function Circle({className = 'size-12'}: Props) {
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
                d='M120 12c59.607 0 108 48.393 108 108s-48.393 108-108 108S12 179.607 12 120 60.393 12 120 12m0 32.041c41.923 0 75.959 34.036 75.959 75.959S161.923 195.959 120 195.959 44.041 161.923 44.041 120 78.077 44.041 120 44.041'
            ></path>
        </svg>
    );
}