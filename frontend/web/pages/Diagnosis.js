import React, { useState, useEffect, useRef } from 'react';
import { Line, Bar } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, PointElement, LineElement } from 'chart.js';
import api from '../axiosInstance';
import '../styles/Diagnosis.css';

ChartJS.register(
  CategoryScale, LinearScale, BarElement,
  Title, Tooltip, Legend, PointElement, LineElement
);

const Diagnosis = () => {
  const [topData, setTopData] = useState(null);
  const [trendData, setTrendData] = useState(null);

  useEffect(() => {
    (async () => {
      try {
        const topRes = await api.get('/admin/diagnosis/top5');
        const topLabels = topRes.data.map(d => d.disease);
        const topCounts = topRes.data.map(d => d.count);
        setTopData({
          labels: topLabels,
          datasets: [{
            label: '발생 건수',
            data: topCounts,
            backgroundColor: 'rgba(75,192,192,.5)',
            borderColor: 'rgba(75,192,192,1)',
            borderWidth: 1
          }]
        });

        const trendRes = await api.get('/admin/diagnosis/weekly', { params: { weeks: 4 } });
        const rows = trendRes.data;
        const weekLabels = [...new Set(rows.map(r => r.yearWeek))].sort();
        const diseases = [...new Set(rows.map(r => r.disease))];

        const dataMap = {};
        rows.forEach(r => {
          dataMap[r.disease] ??= {};
          dataMap[r.disease][r.yearWeek] = r.count;
        });

        const datasets = diseases.map((disease, idx) => ({
          label: `${disease} 발생`,
          data: weekLabels.map(w => dataMap[disease][w] ?? 0),
          fill: false,
          borderColor: `hsl(${idx * 70}, 70%, 50%)`,
          tension: .1,
        }));

        setTrendData({ labels: weekLabels, datasets });

      } catch (err) {
        console.error('통계 조회 실패', err);
        alert('진단 통계 정보를 불러오지 못했습니다.');
      }
    })();
  }, []);

  return (
    <div className="diagnosis-container">
      <h2 className="diagnosis-title">진단 데이터 분석</h2>

      <div className="diagnosis-chart-container">
        <h3>최근 1주일 질환 발생 Top 5</h3>
        {topData && <Bar data={topData} options={{ responsive: true, scales: { y: { ticks: { precision: 0 } } } }} />}
      </div>

      <div className="diagnosis-chart-container" style={{ marginTop: 50 }}>
        <h3>질환 발생률 변화 (최근 4주)</h3>
        {trendData && <Line data={trendData} options={{ responsive: true, scales: { y: { ticks: { precision: 0 } } } }} />}
      </div>
    </div>
  );
};

export default Diagnosis;
