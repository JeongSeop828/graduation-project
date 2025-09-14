import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FaTachometerAlt, FaUserAlt, FaQuestionCircle, FaBrain, FaChartLine, FaClinicMedical, FaSignOutAlt, FaUserShield } from 'react-icons/fa';

const Sidebar = ({ onLogout }) => {
  const [activeItem, setActiveItem] = useState(null);
  const navigate = useNavigate();

  const handleItemClick = (item) => {
    setActiveItem(item);
  };

  const handleLogout = () => {
    const confirmLogout = window.confirm("로그아웃을 하시겠습니까?");
    if (confirmLogout) {
      onLogout(navigate);
    }
  };

  return (
    <div className="sidebar">
      <div className="sidebar-header">
        <h2>멍냥케어 관리</h2>
      </div>
      <ul>
        <li className={activeItem === 'dashboard' ? 'active' : ''} onClick={() => handleItemClick('dashboard')}>
          <Link to="/dashboard"><FaTachometerAlt /> 대시보드</Link>
        </li>
        <li className={activeItem === 'members' ? 'active' : ''} onClick={() => handleItemClick('members')}>
          <Link to="/members"><FaUserAlt /> 회원 관리</Link>
        </li>
        <li className={activeItem === 'admin-management' ? 'active' : ''} onClick={() => handleItemClick('admin-management')}>
          <Link to="/admin-management"><FaUserShield /> 관리자 관리</Link>
        </li>
        <li className={activeItem === 'medicine' ? 'active' : ''} onClick={() => handleItemClick('medicine')}>
          <Link to="/medicine"><FaClinicMedical /> 약품 관리</Link>
        </li>
        <li className={activeItem === 'inquiries' ? 'active' : ''} onClick={() => handleItemClick('inquiries')}>
          <Link to="/inquiries"><FaQuestionCircle /> 문의 내역</Link>
        </li>
        <li className={activeItem === 'diagnosis' ? 'active' : ''} onClick={() => handleItemClick('diagnosis')}>
          <Link to="/diagnosis"><FaChartLine /> 진단 데이터 분석</Link>
        </li>
    
        <li className={activeItem === 'logout' ? 'active' : ''} onClick={handleLogout}>
          <FaSignOutAlt /> 로그아웃
        </li>
      </ul>
    </div>
  );
};

export default Sidebar;
