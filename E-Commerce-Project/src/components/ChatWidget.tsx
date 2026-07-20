import { useContext, useEffect, useRef, useState } from 'react';
import { toast } from 'react-toastify';
import { ShopContext } from '../context/ShopContext';
import { streamChatResponse, type ChatStreamMessage } from '../lib/chatApi';

type ChatMessage = ChatStreamMessage & {
    id: string;
};

const welcomeMessage: ChatMessage = {
    id: 'welcome',
    role: 'assistant',
    content: 'Xin chào, tôi có thể hỗ trợ bạn về sản phẩm, đơn hàng hoặc thao tác trong hệ thống.',
};

const ChatWidget = () => {
    const context = useContext(ShopContext);
    const token = context?.token as string | undefined;
    const [open, setOpen] = useState(false);
    const [messages, setMessages] = useState<ChatMessage[]>([welcomeMessage]);
    const [input, setInput] = useState('');
    const [streaming, setStreaming] = useState(false);
    const abortRef = useRef<AbortController | null>(null);
    const messagesEndRef = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' });
    }, [messages, open]);

    useEffect(() => () => abortRef.current?.abort(), []);

    const sendMessage = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        const text = input.trim();
        if (!text || streaming) return;

        const userMessage: ChatMessage = {
            id: `user-${Date.now()}`,
            role: 'user',
            content: text,
        };
        const assistantMessage: ChatMessage = {
            id: `assistant-${Date.now()}`,
            role: 'assistant',
            content: '',
        };

        const nextMessages = [...messages, userMessage, assistantMessage];
        setMessages(nextMessages);
        setInput('');
        setStreaming(true);

        const controller = new AbortController();
        abortRef.current = controller;

        try {
            await streamChatResponse({
                message: text,
                token,
                signal: controller.signal,
                history: messages
                    .filter((message) => message.id !== 'welcome')
                    .map(({ role, content }) => ({ role, content })),
                onToken: (chunk) => {
                    setMessages((current) =>
                        current.map((message) =>
                            message.id === assistantMessage.id
                                ? { ...message, content: `${message.content}${chunk}` }
                                : message
                        )
                    );
                },
            });
        } catch (error) {
            if ((error as DOMException).name !== 'AbortError') {
                console.error(error);
                toast.error(error instanceof Error ? error.message : 'Không thể kết nối chat');
                setMessages((current) =>
                    current.map((message) =>
                        message.id === assistantMessage.id && !message.content
                            ? { ...message, content: 'Tôi chưa thể phản hồi lúc này. Vui lòng thử lại sau.' }
                            : message
                    )
                );
            }
        } finally {
            setStreaming(false);
            abortRef.current = null;
        }
    };

    const stopStreaming = () => {
        abortRef.current?.abort();
        setStreaming(false);
    };

    return (
        <div className='fixed bottom-5 right-5 z-[95]'>
            {open && (
                <div className='mb-3 flex h-[min(620px,78vh)] w-[min(380px,calc(100vw-40px))] flex-col border border-gray-200 bg-white shadow-2xl'>
                    <div className='flex items-center justify-between border-b border-gray-100 px-4 py-3'>
                        <div>
                            <p className='text-sm font-semibold text-gray-900'>AI Chat</p>
                            <p className='text-xs text-gray-500'>Hỗ trợ user và admin</p>
                        </div>
                        <button
                            type='button'
                            onClick={() => setOpen(false)}
                            className='border border-gray-300 px-3 py-1.5 text-xs font-medium hover:border-black'
                        >
                            Đóng
                        </button>
                    </div>

                    <div className='flex-1 space-y-3 overflow-y-auto bg-gray-50 px-4 py-4'>
                        {messages.map((message) => (
                            <div
                                key={message.id}
                                className={`flex ${message.role === 'user' ? 'justify-end' : 'justify-start'}`}
                            >
                                <div
                                    className={`max-w-[82%] whitespace-pre-wrap px-4 py-2 text-sm leading-6 ${
                                        message.role === 'user'
                                            ? 'bg-black text-white'
                                            : 'border border-gray-200 bg-white text-gray-700'
                                    }`}
                                >
                                    {message.content || (message.role === 'assistant' ? 'Đang trả lời...' : '')}
                                </div>
                            </div>
                        ))}
                        <div ref={messagesEndRef} />
                    </div>

                    <form onSubmit={sendMessage} className='border-t border-gray-100 bg-white p-3'>
                        <div className='flex gap-2'>
                            <textarea
                                value={input}
                                onChange={(event) => setInput(event.target.value)}
                                onKeyDown={(event) => {
                                    if (event.key === 'Enter' && !event.shiftKey) {
                                        event.preventDefault();
                                        event.currentTarget.form?.requestSubmit();
                                    }
                                }}
                                className='min-h-11 max-h-28 flex-1 resize-none border border-gray-300 px-3 py-2 text-sm outline-none focus:border-black'
                                placeholder='Nhập câu hỏi...'
                                disabled={streaming}
                            />
                            {streaming ? (
                                <button
                                    type='button'
                                    onClick={stopStreaming}
                                    className='border border-red-500 px-4 py-2 text-sm font-medium text-red-600 hover:bg-red-500 hover:text-white'
                                >
                                    Dừng
                                </button>
                            ) : (
                                <button
                                    type='submit'
                                    disabled={!input.trim()}
                                    className='bg-black px-4 py-2 text-sm font-medium text-white disabled:bg-gray-300'
                                    aria-label='Gửi tin nhắn'
                                >
                                    <svg
                                        xmlns='http://www.w3.org/2000/svg'
                                        width='18'
                                        height='18'
                                        viewBox='0 0 24 24'
                                        fill='none'
                                        stroke='currentColor'
                                        strokeWidth='2'
                                        strokeLinecap='round'
                                        strokeLinejoin='round'
                                    >
                                        <path d='m22 2-7 20-4-9-9-4Z' />
                                        <path d='M22 2 11 13' />
                                    </svg>
                                </button>
                            )}
                        </div>
                    </form>
                </div>
            )}

            <button
                type='button'
                onClick={() => setOpen((current) => !current)}
                className='flex h-14 w-14 items-center justify-center rounded-full bg-black text-white shadow-xl hover:bg-neutral-800'
                aria-label='Mở AI Chat'
            >
                <svg
                    xmlns='http://www.w3.org/2000/svg'
                    width='24'
                    height='24'
                    viewBox='0 0 24 24'
                    fill='none'
                    stroke='currentColor'
                    strokeWidth='2'
                    strokeLinecap='round'
                    strokeLinejoin='round'
                >
                    <path d='M21 15a4 4 0 0 1-4 4H7l-4 4V7a4 4 0 0 1 4-4h10a4 4 0 0 1 4 4z' />
                    <path d='M8 10h8' />
                    <path d='M8 14h5' />
                </svg>
            </button>
        </div>
    );
};

export default ChatWidget;
