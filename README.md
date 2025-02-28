# 🎬 SNAPSUM - AI 기반 쇼트폼 영상 제작 서비스

**SNAPSUM**은 웹 콘텐츠를 빠르게 요약하고 쇼트폼 영상으로 자동 변환해주는 AI 서비스입니다.
- **SNAP** → "빠르다, 즉각적인"이라는 의미
- **SUM** → "Summary(요약)"를 의미
- 두 단어의 조합으로 **"빠르고 즉각적인 요약 콘텐츠"** 제공

## 🎯 서비스 목표
- 웹페이지 링크만으로 **AI가 자동으로 콘텐츠를 요약**
- **음성 내레이션, 이미지, 텍스트 애니메이션**을 결합한 쇼트폼 영상 자동 생성
- 사용자 친화적인 **미리 듣기 및 영상 다운로드 기능** 제공

## 💡 기대 효과
- **콘텐츠 제작 자동화**: 복잡한 편집 과정 없이 AI가 영상 제작
- **시간 및 비용 절감**: 제작 시간 단축 및 전문 편집 인력 불필요
- **트렌드 활용**: TikTok, YouTube Shorts, Instagram Reels 등 쇼트폼 플랫폼 콘텐츠 제작
- **다양한 활용**: 뉴스, 교육, 마케팅 등 여러 분야에서 활용 가능

## 📌 사용자 플로우
1. **링크 입력**: 사용자가 웹페이지 링크 입력
2. **요약 확인 및 편집**: AI가 생성한 요약 텍스트 확인 및 수정
3. **음성 선택**: 다양한 AI 음성 중 선택 및 미리 듣기
4. **이미지 생성 및 관리**: 텍스트 기반 AI 이미지 생성, 관리
5. **영상 생성**: 요약 텍스트, TTS, 이미지를 조합한 쇼트폼 영상 제작
6. **다운로드**: 완성된 영상 다운로드

## 🚀 주요 기능
- **AI 텍스트 요약**: 웹 콘텐츠의 핵심을 추출하여 간결한 스크립트 생성
- **AI TTS 내레이션**: 자연스러운 음성 내레이션 자동 생성
- **AI 이미지 생성**: 텍스트 기반 관련 이미지 자동 생성
- **자동 영상 편집**: 텍스트, 음성, 이미지를 조합한 쇼트폼 영상 제작
- **비동기 처리**: Spring WebFlux를 활용한 효율적인 API 처리

## 🛠 기술 스택
<img width="668" alt="image" src="https://github.com/user-attachments/assets/6e5c9d67-9d66-480e-9856-1cf1cbbaa952" />

<br/><br/>

## 🏛️ PROJECT ARCHITECTURE
<img width="519" alt="image" src="https://github.com/user-attachments/assets/cd76e385-8896-4856-9d7e-de1e63ade464" />




<br/><br/>

### Elice ML AI 서비스
- **텍스트 요약**: Elice Helpy Pro
- **음성 생성**: Elice Text to Speech API
- **이미지 생성**: Elice Helpy Pro

### 배포
- **Docker + Elice On-Demand**
- **Jenkins** (Freestyle 프로젝트로 CI/CD 구축)

## 📊 구현 현황
- ✅ 웹 콘텐츠 크롤링 및 요약
- ✅ TTS 음성 생성 및 미리듣기
- ✅ AI 이미지 생성 및 관리
- ✅ 쇼트폼 영상 자동 생성
- ✅ 비동기 API 처리 최적화
- ✅ Jenkins를 활용한 자동화된 배포 파이프라인

## DEVELOPERS

<table>
  <tr>
    <td align="center"><a href="https://github.com/YachaTree"><img src="https://avatars.githubusercontent.com/YachaTree" width="150px;" alt="">
    <td align="center"><a href="https://github.com/yujin-zero"><img src="https://avatars.githubusercontent.com/yujin-zero" width="150px;" alt="">
  </tr>
  <tr>
    <td align="center"><strong>🌟김재협(팀장)</strong></td>
    <td align="center"><strong>소유진</strong></td>
  </tr>
    <tr>
    <td align="center"><a href="https://github.com/YachaTree"><b>@YachaTree</b></td>
    <td align="center"><a href="https://github.com/yujin-zero"><b>@yujin-zero</b></td>
  </tr>
   <tr>
    <td align="center">TTS, 동영상 생성</td>
    <td align="center">웹 크롤링, 텍스트 요약, 이미지 생성</td>
  </tr>
</table>

© 2025 Team SumSquad. All rights reserved.
