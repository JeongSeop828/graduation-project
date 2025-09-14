import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../axiosInstance';

const AdminManagement = () => {
  const navigate = useNavigate();
  const [admins, setAdmins] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  // 관리자 목록 불러오기
  useEffect(() => {
    const fetchAdmins = async () => {
      try {
        const res = await axiosInstance.get('/admin/manage');
        setAdmins(res.data);
      } catch (error) {
        console.error('관리자 목록 불러오기 실패:', error);
        alert('관리자 목록을 불러올 수 없습니다.');
      }
    };

    fetchAdmins();
  }, []);

  // 수정 버튼 클릭
  const handleEditClick = (admin) => {
    navigate(`/edit-admin/${admin.id}`);
  };

  // 삭제 버튼 클릭
  const handleDeleteClick = async (adminId) => {
    if (window.confirm('정말로 삭제하시겠습니까?')) {
      try {
        await axiosInstance.delete(`/admin/${adminId}`);
        setAdmins(prev => prev.filter(admin => admin.id !== adminId));
        alert('삭제되었습니다.');
      } catch (error) {
        console.error('삭제 실패:', error);
        alert('삭제 중 오류가 발생했습니다.');
      }
    }
  };

  // 페이지네이션 계산
  const totalPages = Math.ceil(admins.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentAdmins = admins.slice(startIndex, startIndex + itemsPerPage);

  const handlePageChange = (page) => {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
  };

  return (
    <div className="admin-management-wrapper">
      <h2>관리자 관리</h2>
      <table>
        <thead>
          <tr>
            <th>일련번호</th>
            <th>아이디</th>
            <th>이름</th>
            <th>권한</th>
            <th>수정</th>
            <th>삭제</th>
          </tr>
        </thead>
        <tbody>
          {currentAdmins.length === 0 ? (
            <tr>
              <td colSpan="6" style={{ textAlign: 'center' }}>관리자가 없습니다.</td>
            </tr>
          ) : (
            currentAdmins.map((admin, idx) => (
              <tr key={admin.id}>
                <td>{startIndex + idx + 1}</td>
                <td>{admin.username}</td>
                <td>{admin.name}</td>
                <td>{admin.role === 'ADMIN' ? '관리자' : '일반'}</td>
                <td>
                  <button onClick={() => handleEditClick(admin)}>수정</button>
                </td>
                <td>
                  <button onClick={() => handleDeleteClick(admin.id)}>삭제</button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
      {/* 페이지네이션 */}
      {totalPages > 1 && (
        <div style={{ marginTop: '20px' }}>
          <button
            onClick={() => handlePageChange(currentPage - 1)}
            disabled={currentPage === 1}
            style={{ marginRight: '10px' }}
          >
            이전
          </button>
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
          <button
            onClick={() => handlePageChange(currentPage + 1)}
            disabled={currentPage === totalPages}
            style={{ marginLeft: '10px' }}
          >
            다음
          </button>
        </div>
      )}
    </div>
  );
};

export default AdminManagement;
