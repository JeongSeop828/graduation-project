import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axiosInstance from '../axiosInstance'; // 자동 토큰 붙는 인스턴스
import '../styles/EditAdmin.css';

const EditAdmin = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [username, setUsername] = useState('');
  const [name, setName] = useState('');
  const [role, setRole] = useState('');
  const [loading, setLoading] = useState(true);

  // 관리자 정보 불러오기
  useEffect(() => {
    const fetchAdmin = async () => {
      try {
        const res = await axiosInstance.get(`/admin/manage/${id}`);
        setUsername(res.data.username);
        setName(res.data.name);
        setRole(res.data.role); // role: "ADMIN" 또는 "USER"
      } catch (err) {
        alert('관리자 정보를 불러올 수 없습니다.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchAdmin();
  }, [id]);

  const handleSave = async () => {
    try {
      await axiosInstance.put(`/admin/manage/${id}`, {
        name,
        role, // "ADMIN" or "USER"
      });
      alert('관리자 수정 완료');
      navigate('/admin-management');
    } catch (err) {
      alert('수정 실패');
      console.error(err);
    }
  };

  if (loading) return <p>로딩 중...</p>;

  return (
    <div className="edit-admin-wrapper">
      <div className="edit-admin-container">
        <h2>관리자 정보 수정</h2>
        <form>
          <div>
            <label>아이디:</label>
            <input type="text" value={username} disabled />
          </div>

          <div>
            <label>이름:</label>
            <input type="text" value={name} disabled />
          </div>

          <div>
            <label>권한:</label>
            <select value={role} onChange={(e) => setRole(e.target.value)}>
              <option value="ADMIN">관리자</option>
              <option value="USER">일반</option>
            </select>
          </div>

          <button type="button" onClick={handleSave}>저장</button>
          <button type="button" onClick={() => navigate('/admin-management')}>취소</button>
        </form>
      </div>
    </div>
  );
};

export default EditAdmin;
