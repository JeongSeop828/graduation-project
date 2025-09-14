import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../axiosInstance';
import '../styles/InquiryDetail.css';

const InquiryDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [inquiry, setInquiry] = useState(null);
  const [answer, setAnswer] = useState('');

  // 상세 조회
  useEffect(() => {
    const fetchInquiry = async () => {
      try {
        const res = await api.get(`/admin/inquiries/${id}`);
        setInquiry(res.data);
        setAnswer(res.data.reply || '');
      } catch (err) {
        alert('문의 상세를 불러오지 못했습니다.');
        console.error(err);
      }
    };

    fetchInquiry();
  }, [id]);

  if (!inquiry) return <h2>로딩 중...</h2>;

  const handleSubmitAnswer = async () => {
    if (!answer.trim()) {
      alert('답변을 입력해주세요.');
      return;
    }

    try {
      const adminId = 1; // ⚠️ 실제 로그인된 관리자 ID를 사용해야 합니다
      await api.put(`/admin/inquiries/${id}/reply`, {
        adminId,
        reply: answer,
      });

      alert('답변이 등록되었습니다.');
      navigate(-1); // 뒤로 가기
    } catch (err) {
      alert('답변 등록 실패');
      console.error(err);
    }
  };

  return (
    <div className="inquiry-detail-wrapper">
      <div className="inquiry-detail-container">
        <h2>문의 상세 정보</h2>

        <div><strong>닉네임</strong><input type="text" value={inquiry.nickname} disabled /></div>
        <div><strong>제목</strong><input type="text" value={inquiry.title} disabled /></div>
        <div><strong>내용</strong><textarea value={inquiry.content} disabled /></div>
        <div><strong>일시</strong><input type="text" value={new Date(inquiry.createdAt).toLocaleString()} disabled /></div>
        <div><strong>답변 여부</strong><input type="text" value={inquiry.status} disabled /></div>

        <strong>답변</strong>
        <textarea
          value={answer}
          onChange={(e) => setAnswer(e.target.value)}
          placeholder="답변을 입력하세요..."
        />

        <button onClick={handleSubmitAnswer}>답변 제출</button>
        <button className="back" onClick={() => navigate(-1)}>뒤로 가기</button>
      </div>
    </div>
  );
};

export default InquiryDetail;
