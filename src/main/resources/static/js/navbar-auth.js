// 1. common-api.js에서 핵심 함수들을 import
// (경로는 common-api.js와 같은 폴더에 있다고 가정)
import { checkLoginStatus, requestLogout } from '/js/common-api.js';

/**
 * 로그인 상태에 따라 UI를 업데이트하는 함수
 * (navbar 버튼과 home 페이지의 상태 배지를 모두 처리)
 */
function updateAuthUI(isLoggedIn) {
    // --- 1. Navbar 버튼 업데이트 (navbar.html) ---
    const loggedOutView = document.getElementById('logged-out-view');
    const loggedInView = document.getElementById('logged-in-view');

    if (loggedOutView && loggedInView) {
        if (isLoggedIn) {
            // 로그인 상태: "로그아웃" 버튼 표시
            //
            loggedOutView.classList.add('d-none');
            loggedInView.classList.remove('d-none');
        } else {
            // 로그아웃 상태: "로그인", "회원가입" 버튼 표시
            //
            loggedOutView.classList.remove('d-none');
            loggedInView.classList.add('d-none');
        }
    }

    // --- 2. Home 페이지 상태 배지 업데이트 (home.html) ---
    // (home.html이 아닌 다른 페이지에선 이 요소가 null일 수 있으므로 확인)
    const loginStateSpan = document.getElementById('login-state');
    if (loginStateSpan) {
        if (isLoggedIn) {
            loginStateSpan.textContent = '로그인 상태';
            loginStateSpan.className = 'badge bg-success';
        } else {
            loginStateSpan.textContent = '로그아웃 상태';
            loginStateSpan.className = 'badge bg-danger';
        }
    }
}

/**
 * 페이지가 처음 로드될 때 메인 인증 로직 실행
 */
document.addEventListener('DOMContentLoaded', async () => {
    // 1. (★핵심★) common-api를 통해 실제 로그인 상태 확인
    const isLoggedIn = await checkLoginStatus();

    // 2. 확인된 상태로 UI(네비바, 홈 배지) 업데이트
    updateAuthUI(isLoggedIn);

    // 3. (★핵심★) 로그아웃 버튼에 이벤트 연결
    //
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async () => {
            // 버튼 비활성화 (중복 클릭 방지)
            logoutBtn.disabled = true;
            logoutBtn.textContent = '로그아웃 중...';

            // common-api의 로그아웃 함수 호출
            // (이 함수가 AT/RT 삭제 및 페이지 이동까지 처리)
            await requestLogout();

            // requestLogout 함수가 페이지 이동을 하므로,
            // 이 코드는 예비용입니다.
            updateAuthUI(false);
        });
    }
});