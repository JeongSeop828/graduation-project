import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Dashboard from './pages/Dashboard';
import Members from './pages/Members';
import EditMember from './pages/EditMember'
import AdminManagement from './pages/AdminManagement';
import EditAdmin from './pages/EditAdmin';
import Inquiries from './pages/Inquiries';
import InquiryDetail from './pages/InquiryDetail';
import AIModel from './pages/AIModel';
import Diagnosis from './pages/Diagnosis';
import Medicine from './pages/Medicine';
import AddMedicine from './pages/AddMedicine';
import EditMedicine from './pages/EditMedicine';
import Login from './pages/Login';
import Register from './pages/Register';

const App = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  const handleLogin = () => {
    setIsLoggedIn(true); // 토큰은 이미 Login.js에서 저장됨
  };

  const handleLogout = (navigate) => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setIsLoggedIn(false);
    navigate('/login');
  };

  useEffect(() => {
    const accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');
  
    // 토큰이 없다면 자동 로그아웃 처리
    if (!accessToken || !refreshToken) {
      setIsLoggedIn(false);
      return;
    }
  
    // 토큰이 있다면 일단 로그인 상태 유지
    setIsLoggedIn(true);
  }, []);

  useEffect(() => {
    if (isLoggedIn) {
      // 로그인 후에는 Layout.css를 import
      import('./styles/Layout.css');
    }
  }, [isLoggedIn]);

  return (
    <Router>
      <div className="app">
        {isLoggedIn && <Sidebar onLogout={handleLogout} />}  {/* 로그아웃 처리 함수 전달 */}
        <div className="content">
          <Routes>
            <Route
              path="/login"
              element={<Login onLogin={handleLogin} />}  // Login 컴포넌트에 handleLogin 전달
            />
            <Route
              path="/register" // 회원가입 페이지 라우트 추가
              element={<Register />}  // Register 페이지 연결
            />
            {isLoggedIn && (
              <>
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/members" element={<Members />} />
                <Route path="/edit-member/:id" element={<EditMember />} />
                <Route path="/admin-management" element={<AdminManagement />} />
                <Route path="/edit-admin/:id" element={<EditAdmin />} />
                <Route path="/inquiries" element={<Inquiries />} />
                <Route path="/inquiry-detail/:id" element={<InquiryDetail />} />
                <Route path="/ai-model" element={<AIModel />} />
                <Route path="/diagnosis" element={<Diagnosis />} />
                <Route path="/medicine" element={<Medicine />} />
                <Route path="/add-medicine" element={<AddMedicine />} />
                <Route path="/edit-medicine/:id" element={<EditMedicine />} /> {/* 약품 수정 페이지 */}
              </>
            )}
            <Route path="/" element={<Login onLogin={handleLogin} />} /> {/* 기본 경로로 로그인 화면 */}
          </Routes>
        </div>
      </div>
    </Router>
  );
};

export default App;