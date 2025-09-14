import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../axiosInstance';

const Inquiries = () => {
  const [inquiries, setInquiries] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;
  const navigate = useNavigate();

  // 문의 목록 불러오기
  useEffect(() => {
    const fetchInquiries = async () => {
      try {
        const res = await api.get('/admin/inquiries');
        setInquiries(res.data);
      } catch (err) {
        alert('문의 목록을 불러오는 데 실패했습니다.');
        console.error(err);
      }
    };

    fetchInquiries();
  }, []);

  const handleViewDetails = (id) => {
    navigate(`/inquiry-detail/${id}`);
  };

  // 페이지 관련 계산
  const totalPages = Math.ceil(inquiries.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentInquiries = inquiries.slice(startIndex, startIndex + itemsPerPage);

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  return (
    <div>
      <h2>문의 내역 관리</h2>
      <table border="1" cellSpacing="0" cellPadding="10">
        <thead>
          <tr>
            <th>일련번호</th>
            <th>닉네임</th>
            <th>문의 제목</th>
            <th>일시</th>
            <th>자세히 보기</th>
            <th>답변 여부</th>
          </tr>
        </thead>
        <tbody>
          {currentInquiries.map((inquiry) => (
            <tr key={inquiry.id}>
              <td>{inquiry.id}</td>
              <td>{inquiry.nickname}</td>
              <td>{inquiry.title}</td>
              <td>{new Date(inquiry.createdAt).toLocaleString()}</td>
              <td>
                <button onClick={() => handleViewDetails(inquiry.id)}>자세히 보기</button>
              </td>
              <td>{inquiry.status}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* 페이지네이션 버튼 */}
      <div style={{ marginTop: '20px' }}>
        {Array.from({ length: totalPages }, (_, index) => (
          <button
            key={index + 1}
            onClick={() => handlePageChange(index + 1)}
            style={{
              margin: '0 5px',
              fontWeight: currentPage === index + 1 ? 'bold' : 'normal',
            }}
          >
            {index + 1}
          </button>
        ))}
      </div>
    </div>
  );
};

export default Inquiries;
