// API 공통 함수

// localStorage에서 사용할 Access Token의 키
const TOKEN_KEY = 'accessToken';

// AT를 localStorage에 저장하는 함수 (로그인, 토큰 갱신 시 호출)
export function setAccessToken(token) {
    if (token) {
        // [수정] sessionStorage -> localStorage
        localStorage.setItem(TOKEN_KEY, token);
    } else {
        // [수정] sessionStorage -> localStorage
        localStorage.removeItem(TOKEN_KEY);
    }
}

// "인터셉터" 역할을 하는 공통 fetch 함수
export async function fetchWithAuth(url, options = {}) {

    // 헤더 및 credentials 기본값 설정
    if (!options.headers) {
        options.headers = {};
    }
    options.credentials = 'include'; // RT 쿠키를 주고받기 위해 필수

    // (인터셉트) localStorage에 AT가 있으면 헤더에 추가
    // [수정] sessionStorage -> localStorage
    const accessToken = localStorage.getItem(TOKEN_KEY);
    if (accessToken) {
        options.headers['Authorization'] = `Bearer ${accessToken}`;
    }

    // Content-Type 기본값 설정 (필요시)
    if (options.body && !options.headers['Content-Type']) {
        options.headers['Content-Type'] = 'application/json';
    }

    // (첫 번째 시도) API 요청
    let response = await fetch(url, options);

    // AT 만료 이후 재발급 로직
    if (response.status === 401 || response.status === 403) {

        // AT 재발급 요청(/api/auth/refresh) 자체가 401인 경우
        if (url.includes('/api/auth/refresh')) {
            console.error("Refresh Token이 만료되었습니다. 로그아웃 처리합니다.");
            setAccessToken(null); // localStorage 비우기
            // 에러를 발생시켜 catch 블록으로 넘김
            throw new Error('Session expired');
        }

        console.warn("Access Token 만료 감지. 토큰 재발급을 시도합니다.");

        // AT 재발급 시도 (RT 쿠키는 자동으로 전송됨)
        try {
            //
            const refreshResponse = await fetch('/api/auth/refresh', {
                method: 'POST',
                credentials: 'include' // RT 쿠키 전송
            });

            if (refreshResponse.ok) {
                // 재발급 성공
                const data = await refreshResponse.json();
                setAccessToken(data.accessToken); // 새 AT를 localStorage에 저장

                // 원래 요청의 헤더를 새 AT로 교체
                options.headers['Authorization'] = `Bearer ${data.accessToken}`;
                console.log("토큰 재발급 성공. 원래 요청을 재시도합니다.");
                // (두 번째 시도) 원래 요청을 다시 보냄
                response = await fetch(url, options);

            } else {
                // 재발급 실패 (RT 만료)
                console.error("Refresh Token이 만료되었습니다. 로그아웃 처리합니다.");
                setAccessToken(null);
                throw new Error('Session expired');
            }
        } catch (e) {
            console.error("토큰 재발급 중 네트워크 오류:", e);
            setAccessToken(null);
            throw new Error('Session expired');
        }
    }

    // 첫 번째 시도 또는 두 번째 시도(재발급 후)의 응답을 반환

    // 401 이외의 에러(404, 500 등) 또는 재시도 후에도 실패한 경우
    if (!response.ok) {
        // .json()을 시도하고 실패하면 statusText를 사용
        const errorData = await response.json().catch(() => ({
            message: response.statusText || `HTTP error ${response.status}`
        }));

        throw new Error(errorData.message || `API 요청 실패 (Status: ${response.status})`);
    }

    // 최종 성공 응답 반환
    return response;
}

// 페이지 로드 시 로그인 상태 확인 함수 - RT 만료 이후라면 로그아웃
export async function checkLoginStatus() {
    try {
        // '/api/auth/refresh' 호출 시 fetchWithAuth가 자동으로 401 처리
        const response = await fetchWithAuth('/api/auth/refresh', {
            method: 'POST',
        });

        // fetchWithAuth가 에러를 throw하므로, 이 라인에 도달하면 성공
        const data = await response.json();
        setAccessToken(data.accessToken); // localStorage에 새 AT 저장
        return true; // 로그인 상태

    } catch (error) {
        // (Session expired 등)
        console.log("로그인 상태 아님:", error.message);
        setAccessToken(null); // localStorage 비우기
        return false; // 로그아웃 상태
    }
}


// 서버 로그아웃 요청 함수
export async function requestLogout() {
    try {
        //
        await fetchWithAuth('/api/auth/logout', {
            method: 'POST'
        });
    } catch (error) {
        console.error("서버 로그아웃 요청 중 오류:", error);
    } finally {
        // 서버 요청 결과와 상관없이 클라이언트 측 토큰 제거
        setAccessToken(null); // localStorage 비우기
        console.log("클라이언트 로그아웃 완료.");
        window.location.href = '/'; // 홈으로 이동
    }
}