// src/pages/AddMedicine.js
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../axiosInstance';
import '../styles/AddMedicine.css';     // 스타일 재사용

const FIELD_LIST = [
  { key: 'medicineName', label: '약품 이름', placeholder: '예) 말라셉 약용샴푸' },
  { key: 'effect',       label: '효능·효과', placeholder: '예) 항균·항염' },
  { key: 'caution',      label: '주의사항',  placeholder: '예) 도포 후 10분 유지...' }
];

const AddMedicine = () => {
  const navigate = useNavigate();

  /* ---------- 상태 ---------- */
  const [form, setForm] = useState({
    medicineName: '',
    effect: '',
    caution: ''
  });

  /* ---------- 입력 변경 ---------- */
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  /* ---------- 등록 ---------- */
  const handleAdd = async (e) => {
    e.preventDefault();

    const filled = Object.values(form).every(v => v.trim() !== '');
    if (!filled) return alert('모든 필드를 채워주세요!');

    try {
      await axiosInstance.post('/admin/medicines', form);
      alert('약품 추가 완료');
      navigate('/medicine');
    } catch (err) {
      console.error(err);
      alert('추가 실패');
    }
  };

  /* ---------- 렌더 ---------- */
  return (
    <div className="add-medicine-wrapper">
      <div className="add-medicine-container">
        <h2 className="add-medicine-title">새로운 약품 추가</h2>

        <form className="add-medicine-form" onSubmit={handleAdd}>
          {FIELD_LIST.map(({ key, label, placeholder }) => (
            <div className="form-group" key={key}>
              <label>{label}</label>
              <input
                name={key}
                value={form[key]}
                onChange={handleChange}
                placeholder={placeholder}
                className="input-field"
              />
            </div>
          ))}

          <button type="submit" className="add-button">
            약품 추가
          </button>
        </form>
      </div>
    </div>
  );
};

export default AddMedicine;
