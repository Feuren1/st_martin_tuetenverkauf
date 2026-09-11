import { useMemo } from 'react'
import './App.css'

const HEART_COUNT = 28

function App() {
  const hearts = useMemo(
    () =>
      Array.from({ length: HEART_COUNT }, (_, i) => ({
        id: i,
        left: Math.random() * 100,
        size: 16 + Math.random() * 40,
        duration: 3 + Math.random() * 4,
        delay: Math.random() * 5,
        drift: (Math.random() - 0.5) * 60,
      })),
    [],
  )

  return (
    <div className="love-page">
      {hearts.map((h) => (
        <span
          key={h.id}
          className="floating-heart"
          style={
            {
              left: `${h.left}%`,
              fontSize: `${h.size}px`,
              animationDuration: `${h.duration}s`,
              animationDelay: `${h.delay}s`,
              '--drift': `${h.drift}px`,
            } as React.CSSProperties
          }
        >
          ❤
        </span>
      ))}

      <div className="love-message">
        <span className="pulsing-heart big-heart">❤</span>
        <h1>Ich liebe dich pupsi</h1>
        <span className="pulsing-heart big-heart">❤</span>
      </div>
    </div>
  )
}

export default App
