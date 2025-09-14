import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../axiosInstance';
import '../styles/Login.css';

const Login = ({ onLogin }) => {
  const [userId, setUserId] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    document.body.classList.add('login-page');
    return () => document.body.classList.remove('login-page');
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const res = await api.post('/admin/login', {
        username: userId,
        password: password,
      });

      const { accessToken, refreshToken } = res.data;
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);

      if (onLogin) onLogin();
      navigate('/dashboard');
    } catch (err) {
      setError('아이디 또는 비밀번호가 잘못되었습니다.');
    }
  };

  return (
    <div className="login-form-container">
      <h1><a href="/" className="login-title-link">멍냥케어</a></h1>

      <form onSubmit={handleSubmit}>
        <div>
          <input
            type="text"
            placeholder="아이디"
            required
            value={userId}
            onChange={(e) => setUserId(e.target.value)}
          />
        </div>
        <div>
          <input
            type="password"
            placeholder="비밀번호"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <button type="submit">로그인</button>
        {error && <p className="login-error-message">{error}</p>}
      </form>

      <Link to="/register" className="login-register-link">회원가입</Link>
    </div>
  );
};

export default Login;
