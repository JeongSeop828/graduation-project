import React, { useState } from 'react';
import '../styles/AIModel.css'

const AIModel = () => {
  const [trainingInProgress, setTrainingInProgress] = useState(false); // 모델 재학습 진행 상태
  const [progress, setProgress] = useState(0); // 진행 상태
  const [lastTrained, setLastTrained] = useState('2025-03-28 오후 2:00'); // 마지막 재학습 일시
  const [trainingCompleted, setTrainingCompleted] = useState(false); // 재학습 완료 상태

  // 모델 재학습 시작 함수
  const startTraining = () => {
    setTrainingInProgress(true);
    setTrainingCompleted(false); // 재학습 완료 상태 초기화
    let currentProgress = 0;

    // 모델 재학습 진행바 업데이트
    const interval = setInterval(() => {
      currentProgress += 0.02778; // 0.1667%씩 증가 (10분 동안 100%)
      setProgress(currentProgress);

      // 재학습 완료 시
      if (currentProgress >= 100) {
        clearInterval(interval);
        setTrainingInProgress(false);
        setLastTrained(new Date().toLocaleString()); // 재학습 일시 갱신
        setTrainingCompleted(true); // 재학습 완료 상태로 설정
      }
    }, 1000); // 1초마다 진행 상황 업데이트
  };

  return (
    <div className='ai-model-container'>
      <h2 className='ai-model-title'>AI 모델 관리</h2>

      <div className='ai-model-layout'>

      {/* 모델 정보 */}
      <div className='model-info'>
        <p className='model-last-trained'><strong>마지막 재학습 일시:</strong> {lastTrained}</p>

      {/* 재학습 버튼 */}
      <button className='start-training-btn'
        onClick={startTraining}
        disabled={trainingInProgress} // 재학습 중에는 버튼 비활성화
        style={{ padding: '10px 20px',marginTop: '10px', backgroundColor: '#4caf50', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
      >
        {trainingInProgress ? '재학습 진행 중...' : '모델 재학습'}
      </button>

      {/* 진행 바 */}
      {trainingInProgress && (
        <div className='progress-bar-container' style={{ marginTop: '10px', width: '100%', backgroundColor: '#ccc', borderRadius: '5px' }}>
          <div className='progress-bar'
            style={{
              height: '20px',
              width: `${progress}%`,
              backgroundColor: '#4caf50',
              borderRadius: '5px',
              transition: 'width 1s ease-out',
            }}
          ></div>
          </div>
      )}

      {/* 재학습 완료 알림 */}
      {trainingCompleted && (
        <div className='training-completed-alert' style={{ marginTop: '10px', padding: '10px', backgroundColor: '#4caf50', color: 'white', borderRadius: '4px' }}>
          <strong>재학습 완료!</strong> 모델이 성공적으로 재학습되었습니다.
        </div>
      )}
    </div>
    </div>
    </div>
  );
};

export default AIModel;
