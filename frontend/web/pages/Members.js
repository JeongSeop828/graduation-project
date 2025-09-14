import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Members.css';
import api from '../axiosInstance';

const Members = () => {
  const [members, setMembers] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;
  const navigate = useNavigate();

  useEffect(() => {
    fetchMembers();
  }, []);

  const fetchMembers = async () => {
    try {
      const res = await api.get('/admin/users');
      setMembers(res.data);
    } catch (error) {
      console.error('회원 목록 불러오기 실패:', error);
    }
  };

  const handleEdit = (id) => {
    navigate(`/edit-member/${id}`);
  };

  const handleDelete = async (id) => {
    const confirmDelete = window.confirm('정말로 삭제하시겠습니까?');
    if (!confirmDelete) return;

    try {
      await api.delete(`/admin/users/${id}`);
      setMembers(members.filter((m) => m.id !== id));
      alert('삭제되었습니다.');
    } catch (error) {
      console.error('삭제 실패:', error);
      alert('삭제 중 오류 발생');
    }
  };

  // 페이징 관련 계산
  const totalPages = Math.ceil(members.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentMembers = members.slice(startIndex, startIndex + itemsPerPage);

  const handlePageChange = (page) => {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
  };

  return (
    <div>
      <h2>회원 관리</h2>
      <table border="1" cellSpacing="0" cellPadding="10">
        <thead>
          <tr>
            <th>일련번호</th>
            <th>아이디</th>
            <th>닉네임</th>
            <th>등록 반려동물 수</th>
            <th>수정</th>
            <th>삭제</th>
          </tr>
        </thead>
        <tbody>
          {currentMembers.length === 0 ? (
            <tr>
              <td colSpan="6" style={{ textAlign: 'center' }}>회원이 없습니다.</td>
            </tr>
          ) : (
            currentMembers.map((member) => (
              <tr key={member.id}>
                <td>{member.id}</td>
                <td>{member.username}</td>
                <td>{member.nickname}</td>
                <td>{member.petCount}</td>
                <td>
                  <button onClick={() => handleEdit(member.id)}>수정</button>
                </td>
                <td>
                  <button onClick={() => handleDelete(member.id)}>삭제</button>
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

export default Members;


// import React, { useEffect, useState } from 'react';
// import { useNavigate } from 'react-router-dom';
// import '../styles/Members.css';
// import api from '../axiosInstance'; // ✅ api → api로 변경 (자동 재발급 적용됨)

// const Members = () => {
//   const [members, setMembers] = useState([]);
//   const navigate = useNavigate();

//   useEffect(() => {
//     fetchMembers();
//   }, []);
  
//   const fetchMembers = async () => {
//     try {
//       const res = await api.get('/admin/users'); // ✅ token 자동 처리
//       setMembers(res.data);
//     } catch (error) {
//       console.error('회원 목록 불러오기 실패:', error);
//     }
//   };

//   const handleEdit = (id) => {
//     navigate(`/edit-member/${id}`);
//   };

//   const handleDelete = async (id) => {
//     const confirmDelete = window.confirm('정말로 삭제하시겠습니까?');
//     if (!confirmDelete) return;

//     try {
//       await api.delete(`/admin/users/${id}`); // ✅ token 자동 처리
//       setMembers(members.filter((m) => m.id !== id));
//       alert('삭제되었습니다.');
//     } catch (error) {
//       console.error('삭제 실패:', error);
//       alert('삭제 중 오류 발생');
//     }
//   };

//   return (
//     <div>
//       <h2>회원 관리</h2>
//       <table border="1" cellSpacing="0" cellPadding="10">
//         <thead>
//           <tr>
//             <th>일련번호</th>
//             <th>아이디</th>
//             <th>닉네임</th>
//             <th>등록 반려동물 수</th>
//             <th>수정</th>
//             <th>삭제</th>
//           </tr>
//         </thead>
//         <tbody>
//           {members.map((member) => (
//             <tr key={member.id}>
//               <td>{member.id}</td>
//               <td>{member.username}</td>
//               <td>{member.nickname}</td>
//               <td>{member.petCount}</td>
//               <td>
//                 <button onClick={() => handleEdit(member.id)}>수정</button>
//               </td>
//               <td>
//                 <button onClick={() => handleDelete(member.id)}>삭제</button>
//               </td>
//             </tr>
//           ))}
//         </tbody>
//       </table>
//     </div>
//   );
// };

// export default Members;
