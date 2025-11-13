// auth.js (수정됨)

// UI 요소를 캐시하는 함수
function getUIElements() {
    return {
        loggedOutView: document.getElementById('logged-out-view'),
        loggedInView: document.getElementById('logged-in-view'),
        logoutBtn: document.getElementById('logout-btn'),
        loginStateDisplay: document.getElementById('login-state') // 홈화면 상태 표시 요소
    };
}

/**
 * Access Token의 유효성에 따라 네비게이션바 및 홈화면 UI를 업데이트합니다.
 * @param {boolean} isAuthenticated - 토큰이 유효한지 여부
 * @param {object} elements - UI 요소들
 */
function updateAuthStateUI(isAuthenticated, elements) {
    const { loggedOutView, loggedInView, loginStateDisplay } = elements;

    if (isAuthenticated) {
        // 네비게이션바: 로그인 상태 (로그아웃 버튼 표시)
        if (loggedOutView) loggedOutView.classList.add('d-none');
        if (loggedInView) loggedInView.classList.remove('d-none');

        // 홈화면 상태 표시
        if (loginStateDisplay) {
            loginStateDisplay.textContent = '로그인됨 (Access Token 유효)';
            loginStateDisplay.classList.remove('bg-secondary', 'bg-danger');
            loginStateDisplay.classList.add('bg-success');
        }
    } else {
        // 네비게이션바: 로그아웃 상태 (로그인/회원가입 버튼 표시)
        if (loggedOutView) loggedOutView.classList.remove('d-none');
        if (loggedInView) loggedInView.classList.add('d-none');

        // 홈화면 상태 표시
        if (loginStateDisplay) {
            loginStateDisplay.textContent = '로그아웃됨 (Access Token 없음/무효)';
            loginStateDisplay.classList.remove('bg-success');
            loginStateDisplay.classList.add('bg-danger');
        }
    }
}

/**
 * Access Token이 있으면 서버에 유효성 검사를 요청합니다.
 */
async function checkTokenValidity() {
    const elements = getUIElements();
    const accessToken = localStorage.getItem('accessToken');

    // 1. Access Token 자체가 없다면 바로 로그아웃 상태 UI 표시
    if (!accessToken) {
        updateAuthStateUI(false, elements);
        return;
    }

    // 2. Access Token이 있다면 서버에 유효성 검사 요청 (예: /api/me)
    try {
        const response = await fetch('/api/me', {
            method: 'GET',
            headers: {
                // 토큰을 Authorization 헤더에 Bearer 타입으로 담아 보냅니다.
                'Authorization': `Bearer ${accessToken}`
            }
        });

        if (response.ok) {
            // 서버가 200 OK를 반환: 토큰 유효
            updateAuthStateUI(true, elements);
        } else if (response.status === 401 || response.status === 403) {
            // 서버가 401 (Unauthorized) 또는 403 (Forbidden) 반환: 토큰 무효
            // 로컬 스토리지에서 Access Token 제거하여 강제 로그아웃 처리
            localStorage.removeItem('accessToken');
            updateAuthStateUI(false, elements);
        } else {
            // 기타 서버 오류 (500 등), 일단 무효 처리하여 재로그인 유도
            localStorage.removeItem('accessToken');
            updateAuthStateUI(false, elements);
        }

    } catch (error) {
        // 네트워크 오류 (서버 꺼짐, CORS 등)
        console.error('토큰 유효성 검사 중 네트워크 오류 발생:', error);
        // 서버가 꺼져있을 경우에도 강제로 로컬 토큰을 제거하여 초기화
        localStorage.removeItem('accessToken');
        updateAuthStateUI(false, elements);
    }
}


document.addEventListener('DOMContentLoaded', function() {
    // 페이지 로드 시 토큰 유효성 검사 및 UI 설정 시작
    checkTokenValidity();

    const logoutBtn = document.getElementById('logout-btn');

    // 로그아웃 버튼 클릭 이벤트 (기존 로직 유지)
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function() {
            logoutBtn.disabled = true;
            logoutBtn.textContent = '로그아웃 중...';

            fetch('/api/auth/logout', {
                method: 'POST',
                credentials: 'include'
            })
                .then(response => {
                    // 서버 응답 상태와 관계없이 클라이언트 측 토큰 제거
                    localStorage.removeItem('accessToken');
                    alert('로그아웃되었습니다.');
                    window.location.href = '/';
                })
                .catch(error => {
                    console.error('로그아웃 오류:', error);
                    localStorage.removeItem('accessToken');
                    alert('로그아웃되었습니다.');
                    window.location.href = '/';
                });
        });
    }
});