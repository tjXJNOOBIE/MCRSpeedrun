import {useEffect, useState} from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import {data} from "autoprefixer";

function App() {
  const [count, setCount] = useState(0)
    const [count2, setCount3] = useState(0)
    const [message, setMessage] = useState("Loading...");

    useEffect(() => {
        fetch("/api/test")
            .then(res => res.text())
            .then(data => setMessage(data))
            .catch(err => console.error(err));
    }, []);

  return (
      <>
          <div>
              <h1>Frontend + Backend Test</h1>
              <p>Message from backend: {message}</p>
              <a href="https://vite.dev" target="_blank">
                  <img src={viteLogo} className="logo" alt="Vite logo"/>
              </a>
              <a href="https://react.dev" target="_blank">
                  <img src={reactLogo} className="logo react" alt="React logo"/>
              </a>
          </div>
          <h1 className="text-3xl font-sans ">
              Hello world!
              <button onClick={() => setCount3((count2) => count2 + 3)}>
                  count is {count2}!
              </button>
          </h1>
          <h1>Vite + React</h1>
          <div className="card">
              <button onClick={() => setCount((count) => count + 3)}>
                  count is {count}!
              </button>

              <p>
                  Edit <code>src/App.jsx</code> and save to test HMR
              </p>
          </div>
          <p className="read-the-docs">
              Click on the Vite and React logos to learn more
          </p>
      </>
  )
}

export default App
