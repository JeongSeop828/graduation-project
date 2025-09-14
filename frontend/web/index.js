import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";  // App.js에서 컴포넌트를 임포트합니다.
import "./index.css";  // 스타일 파일이 필요하면 추가

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
