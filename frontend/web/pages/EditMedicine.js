// src/pages/EditMedicine.js
import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axiosInstance from '../axiosInstance';
import '../styles/EditMedicine.css';

const FIELD_LIST = [
  { key: 'medicineName', label: '약품 이름' },
  { key: 'effect',       label: '효능·효과' },
  { key: 'caution',      label: '주의사항' }
];

const EditMedicine = () => {
  const navigate = useNavigate();
  const { id } = useParams();

  /* ---------- 상태 ---------- */
  const [medicine, setMedicine] = useState({
    medicineName: '',
    effect: '',
    caution: ''
  });

  /* ---------- 초기 데이터 로딩 ---------- */
  useEffect(() => {
    const fetchMedicine = async () => {
      try {
        const { data } = await axiosInstance.get(`/admin/medicines/${id}`);
        setMedicine({
          medicineName: data.medicineName ?? '',
          effect:       data.effect       ?? '',
          caution:      data.caution      ?? ''
        });
      } catch (err) {
        console.error(err);
        alert('약품 정보를 불러올 수 없습니다.');
      }
    };
    fetchMedicine();
  }, [id]);

  /* ---------- 입력 핸들러 ---------- */
  const handleInputChange = e => {
    const { name, value } = e.target;
    setMedicine(prev => ({ ...prev, [name]: value }));
  };

  /* ---------- 저장 ---------- */
  const handleSave = async () => {
    const allFilled = Object.values(medicine).every(v => v.trim() !== '');
    if (!allFilled) return alert('모든 필드를 채워주세요!');

    try {
      await axiosInstance.put(`/admin/medicines/${id}`, medicine);
      alert('약품 수정 완료');
      navigate('/medicine');
    } catch (err) {
      console.error(err);
      alert('수정 실패');
    }
  };

  /* ---------- 렌더 ---------- */
  return (
    <div className="edit-medicine-wrapper">
      <div className="edit-medicine-container">
        <h2 className="edit-medicine-title">약품 수정</h2>

        <form className="edit-medicine-form" onSubmit={e => e.preventDefault()}>
          {FIELD_LIST.map(({ key, label }) => (
            <div className="form-group" key={key}>
              <label>{label}</label>
              <input
                name={key}
                value={medicine[key]}
                onChange={handleInputChange}
                placeholder={label}
                className="input-field"
              />
            </div>
          ))}

          <button
            type="button"
            onClick={handleSave}
            className="edit-medicine-button"
          >
            저장
          </button>
        </form>
      </div>
    </div>
  );
};

export default EditMedicine;
