import React, { useState, useEffect } from 'react';
import api from '../axiosInstance'; // ✅ 자동 재발급 적용된 api 인스턴스
import { useParams, useNavigate } from 'react-router-dom';
import '../styles/EditMember.css';

const EditMember = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [nickname, setNickname] = useState('');
  const [petCount, setPetCount] = useState(0);
  const [username, setUsername] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await api.get(`/admin/users/${id}`); // ✅ 자동 토큰 처리됨
        const data = res.data;
        setUsername(data.username);
        setNickname(data.nickname);
        setPetCount(data.petCount);
      } catch (error) {
        alert('회원 정보를 불러올 수 없습니다.');
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, [id]);

  const handleSave = async () => {
    try {
      await api.put(`/admin/users/${id}`, {
        nickname,
        petCount,
      }); // ✅ 토큰 자동 포함
      alert('회원 수정 완료');
      navigate('/members');
    } catch (error) {
      if (
        error.response &&
        typeof error.response.data === 'string' &&
        error.response.data.includes('중복된 닉네임')
      ) {
        alert('이미 사용 중인 닉네임입니다.');
      } else {
        alert('회원 수정 실패');
      }
      console.error(error);
    }
  };

  if (loading) return <p>로딩 중...</p>;

  return (
    <div className="edit-member-wrapper">
      <div className="edit-member-container">
        <h2>회원 정보 수정</h2>
        <form>
          <div>
            <label>아이디:</label>
            <input type="text" value={username} disabled />
          </div>
          <div>
            <label>닉네임:</label>
            <input
              type="text"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
            />
          </div>
          <div>
            <label>반려동물 수:</label>
            <input
              type="number"
              value={petCount}
              onChange={(e) => setPetCount(Number(e.target.value))}
            />
          </div>
          <button type="button" onClick={handleSave}>저장</button>
          <button type="button" onClick={() => navigate('/members')}>취소</button>
        </form>
      </div>
    </div>
  );
};

export default EditMember;
