import React, { useRef, useState, useEffect } from 'react';
import axiosInstance from '../axiosInstance';
import { Line } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, LineElement, Title, Tooltip, Legend } from 'chart.js';
import "../styles/Dashboard.css";

ChartJS.register(CategoryScale, LinearScale, LineElement, Title, Tooltip, Legend);

const Dashboard = () => {
  const chartRef = useRef(null);

  const [stats, setStats] = useState({
    todaySignups: 0,
    totalUsers: 0,
    dailyActiveUsers: 0,
    weeklyVisitors: {},
  });

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const res = await axiosInstance.get('/admin/dashboard');
        setStats(res.data);
      } catch (error) {
        console.error('대시보드 데이터 로딩 실패:', error);
      }
    };

    fetchStats();
  }, []);

// 주의: stats.weeklyVisitors는 날짜 key를 가진 객체임
const weeklyVisitorEntries = Object.entries(stats.weeklyVisitors || {}); // [["2025-04-14", 2], ["2025-04-15", 3], ...]

weeklyVisitorEntries.sort(([dateA], [dateB]) => new Date(dateA) - new Date(dateB));

const labels = weeklyVisitorEntries.map(([date]) => date);       // ["2025-04-14", "2025-04-15", ...]
const data = weeklyVisitorEntries.map(([, count]) => count);     // [2, 3, ...]

const dailyVisitorsData = {
  labels: labels,
  datasets: [
    {
      label: '일일 접속자 수',
      data: data,
      backgroundColor: 'rgba(75, 192, 192, 0.2)',
      borderColor: 'rgba(75, 192, 192, 1)',
      borderWidth: 1,
      fill: true,
    },
  ],
};

  return (
    <div className="dashboard">
      <h1 className="dashboard-title">대시보드</h1>
      <div className='dashboard-layout'>
      <div className="stats">
        <div className="stat-item">
        <h3>오늘 가입자 수</h3>
        <p>{stats.todaySignups}명</p>
      </div>
        <div className="stat-item">
        <h3>총 가입자 수</h3>
        <p>{stats.totalUsers}명</p>
      </div>
      <div className="stat-item">
      <h3>일일 접속자 수</h3>
        <p>{stats.dailyActiveUsers}명</p>
      </div>

      </div>

      <div className="chart-container">
        <h3>일일 접속자 통계</h3>
        <Line
          ref={chartRef}  // 차트 참조 추가
          data={dailyVisitorsData}
          options={{
            responsive: true,  // 반응형으로 크기 조정
            maintainAspectRatio: false,  // 고정된 비율로 유지하지 않음
            layout: {
              padding: {  // 차트 상단 여백 추가
                right: 20,  // 차트 우측 여백 추가
                bottom: 50,  // 차트 하단 여백 추가
                left: 20,  // 차트 좌측 여백 추가
              },
            },
            scales: {
              x: {
                ticks: {
                  padding: 10,  // x축 텍스트 여백 추가
                },
                grid: {
                  display: false, // x축 격자선 숨김
                },
              },
              y: {
                min: 0,
                ticks: {
                  padding: 10,  // y축 텍스트 여백 추가
                  callback: function(value) {
                    return value % 1 === 0 ? value : ''; // 소숫점 제거 (정수만 표시)
                  },
                },
                grid: {
                  display: false, // y축 격자선 숨김
                },
              },
            },
          }}
        />
      </div>
    </div>
    </div>
  );
};

export default Dashboard;
