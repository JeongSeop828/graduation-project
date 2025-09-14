import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../axiosInstance';

const Medicine = () => {
  const navigate = useNavigate();
  const [medicines, setMedicines] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  /* ---------- 데이터 로드 ---------- */
  useEffect(() => { fetchMedicines(); }, []);

  const fetchMedicines = async () => {
    try {
      const res = await axiosInstance.get('/admin/medicines');
      setMedicines(res.data);
    } catch (err) {
      console.error('약품 목록 조회 실패:', err);
      alert('약품 목록을 불러오는 데 실패했습니다.');
    }
  };

  /* ---------- CRUD ---------- */
  const handleEditClick   = (m)  => navigate(`/edit-medicine/${m.id}`);
  const handleAddMedicine = ()   => navigate('/add-medicine');
  const handleDeleteClick = async (id) => {
    if (!window.confirm('정말로 삭제하시겠습니까?')) return;
    try {
      await axiosInstance.delete(`/admin/medicines/${id}`);
      setMedicines(prev => prev.filter(m => m.id !== id));
      alert('삭제되었습니다.');
    } catch (err) {
      console.error('삭제 실패:', err);
      alert('삭제 중 오류가 발생했습니다.');
    }
  };

  // 페이지네이션 계산
  const totalPages = Math.ceil(medicines.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentMedicines = medicines.slice(startIndex, startIndex + itemsPerPage);

  const handlePageChange = (page) => {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
  };

  /* ---------- 화면 ---------- */
  return (
    <div className="medicine-container">
      <h2 className="medicine-title">약품 관리</h2>

      <div className="medicine-layout">
        <button 
          onClick={handleAddMedicine} 
          className="add-medicine-button"
          style={{
            padding: '5px 10px',
            backgroundColor: '#4caf50',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            fontSize: '16px',
          }}
        >
          약품 추가
        </button>

        <table className="medicine-table">
          <thead>
            <tr>
              <th>일련번호</th>
              <th>약품 이름</th>
              <th>효능/효과</th>
              <th>주의사항</th>
              <th>수정</th>
              <th>삭제</th>
            </tr>
          </thead>
          <tbody>
            {currentMedicines.length === 0 ? (
              <tr>
                <td colSpan="6" style={{ textAlign: 'center' }}>약품이 없습니다.</td>
              </tr>
            ) : (
              currentMedicines.map((m, idx) => (
                <tr key={m.id}>
                  <td>{startIndex + idx + 1}</td>
                  <td>{m.medicineName}</td>
                  <td>{m.effect}</td>
                  <td>{m.caution}</td>
                  <td>
                    <button onClick={() => handleEditClick(m)}>수정</button>
                  </td>
                  <td>
                    <button onClick={() => handleDeleteClick(m.id)}>삭제</button>
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
    </div>
  );
};

export default Medicine;


// // src/pages/Medicine.js
// import React, { useState, useEffect } from 'react';
// import { useNavigate } from 'react-router-dom';
// import axiosInstance from '../axiosInstance';

// const Medicine = () => {
//   const navigate = useNavigate();
//   const [medicines, setMedicines] = useState([]);

//   /* ---------- 데이터 로드 ---------- */
//   useEffect(() => { fetchMedicines(); }, []);

//   const fetchMedicines = async () => {
//     try {
//       const res = await axiosInstance.get('/admin/medicines');
//       setMedicines(res.data);               // ← 배열 그대로
//     } catch (err) {
//       console.error('약품 목록 조회 실패:', err);
//       alert('약품 목록을 불러오는 데 실패했습니다.');
//     }
//   };

//   /* ---------- CRUD ---------- */
//   const handleEditClick   = (m)  => navigate(`/edit-medicine/${m.id}`);
//   const handleAddMedicine = ()   => navigate('/add-medicine');
//   const handleDeleteClick = async (id) => {
//     if (!window.confirm('정말로 삭제하시겠습니까?')) return;
//     try {
//       await axiosInstance.delete(`/admin/medicines/${id}`);
//       setMedicines(prev => prev.filter(m => m.id !== id));
//       alert('삭제되었습니다.');
//     } catch (err) {
//       console.error('삭제 실패:', err);
//       alert('삭제 중 오류가 발생했습니다.');
//     }
//   };

//   /* ---------- 화면 ---------- */
//   return (
//     <div className="medicine-container">
//       <h2 className="medicine-title">약품 관리</h2>

//       <div className="medicine-layout">
//       <button 
//           onClick={handleAddMedicine} 
//           className="add-medicine-button"
//           style={{
//             padding: '5px 10px',
//             backgroundColor: '#4caf50',
//             border: 'none',
//             borderRadius: '4px',
//             cursor: 'pointer',
//             fontSize: '16px',
//           }}
//         >
//           약품 추가
//         </button>

//         <table className="medicine-table">
//           <thead>
//             <tr>
//               <th>일련번호</th>
//               <th>약품 이름</th>
//               <th>효능/효과</th>
//               <th>주의사항</th>
//               <th>수정</th>
//               <th>삭제</th>
//             </tr>
//           </thead>

//           <tbody>
//             {medicines.map((m, idx) => (
//               <tr key={m.id}>
//                 <td>{idx + 1}</td>
//                 <td>{m.medicineName}</td>
//                 <td>{m.effect}</td>
//                 <td>{m.caution}</td>

//                 <td>
//                   <button onClick={() => handleEditClick(m)}>수정</button>
//                 </td>
//                 <td>
//                   <button onClick={() => handleDeleteClick(m.id)}>삭제</button>
//                 </td>
//               </tr>
//             ))}
//           </tbody>
//         </table>
//       </div>
//     </div>
//   );
// };

// export default Medicine;
