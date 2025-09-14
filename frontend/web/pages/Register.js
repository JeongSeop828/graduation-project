import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom'; 
import '../styles/Register.css';

const Register = () => {
  const [userId, setUserId] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [name, setName] = useState('');
  const [isIdChecked, setIsIdChecked] = useState(false);
  const [error, setError] = useState('');
  const [idError, setIdError] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [idAvailableMessage, setIdAvailableMessage] = useState('');

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    if (!isIdChecked) {
      setIdError('아이디 중복확인을 해주세요.');
      return;
    } else {
      setIdError('');
    }
  
    if (password !== confirmPassword) {
      setPasswordError('비밀번호가 일치하지 않습니다.');
      return;
    } else {
      setPasswordError('');
    }
  
    if (!userId || !password || !name) {
      setError('모든 필드를 입력하세요.');
      return;
    }
  
    setError('');
  
    try {
      const response = await fetch('http://localhost:8080/admin/signup', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: userId,
          password: password,
          name: name,
        }),
      });
  
      if (response.ok) {
        alert('회원가입이 완료되었습니다!');
        navigate('/login');
      } else {
        const data = await response.text();
        setError(data || '회원가입 실패');
      }
    } catch (err) {
      console.error('서버 오류:', err);
      setError('서버와의 연결 중 오류가 발생했습니다.');
    }
  };
  
  const handleIdCheck = async () => {
    if (!userId) {
      setIdError('아이디를 입력하세요.');
      return;
    }
  
    try {
      const res = await fetch(`http://localhost:8080/admin/check-id?username=${userId}`);
      const data = await res.json();
  
      if (data.exists) {
        setIdError('이미 존재하는 아이디입니다.');
        setIsIdChecked(false);
        setIdAvailableMessage('');
      } else {
        setIdError('');
        setIsIdChecked(true);
        setIdAvailableMessage('사용 가능한 아이디입니다.');
      }
    } catch (err) {
      console.error('중복 확인 실패:', err);
      setIdError('중복 확인 중 오류가 발생했습니다.');
    }
  };
  
  

  return (
    <div className="register-page">
    <div className="register-container">
      <div className="form-container">
        <h1><Link to="/" className="title-link">회원가입</Link></h1>

        <form onSubmit={handleSubmit}>
          <div className="input-container">
            <label htmlFor="user_id">아이디</label>
            <div className="input-wrapper">
              <input
                type="text"
                id="user_id"
                name="user_id"
                placeholder="아이디를 입력하세요"
                value={userId}
                onChange={(e) => setUserId(e.target.value)}
                required
              />
              <button type="button" onClick={handleIdCheck} className="id-check-btn">
                중복확인
              </button>
            </div>
            {idError && <p className="error-message">{idError}</p>}
            {idAvailableMessage && <p className="success-message">{idAvailableMessage}</p>}
          </div>

          <div>
            <label htmlFor="password">비밀번호</label>
            <input
              type="password"
              id="password"
              name="password"
              placeholder="비밀번호를 입력하세요"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <div>
            <label htmlFor="confirmPassword">비밀번호 재입력</label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              placeholder="비밀번호를 다시 입력하세요"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
            />
            {passwordError && <p className="error-message">{passwordError}</p>}
          </div>

          <div>
            <label htmlFor="name">이름</label>
            <input
              type="text"
              id="name"
              name="name"
              placeholder="이름을 입력하세요"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
          </div>

          <button type="submit">회원가입</button>

          {error && <p className="error-message">{error}</p>}
        </form>

        <Link to="/login" className="login-link">로그인</Link>
      </div>
    </div>
    </div>
  );
};

export default Register;
