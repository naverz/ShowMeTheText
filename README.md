# 프로젝트 목표
안드로이드에서 텍스트와 관련된 디자인 작업 시 매번 겪는 문제가 존재합니다. 제플린 / 피그마와 같은 디자인 툴에서 제공하는 텍스트 컴포넌트와 안드로이드의 텍스트 컴포넌트 간 미세한 간격차이가 존재하여 가이드의 수치대로 작업을 진행하여도 디자이너가 의도했던 결과물대로 구현되지 않는 현상입니다.

이 문제로 인해 텍스트 간격을 맞추고자 반복적인 커뮤니케이션 비용이 발생하고 있습니다. 이를 해결하기 위해 안드로이드상에서 실제로 표현되는 텍스트 컴포넌트를 동적으로 확인할 수 있는 앱을 제공하여, 간격 보정 및 테스트를 진행하는 과정에 들어가는 에너지를 줄이고자 하였습니다.

해당 앱은 실제 안드로이드 텍스트 컴포넌트를 그대로 화면 최상단에 띄워주는 역할을 합니다. 텍스트 크기, 굵기, 행간 그리고 텍스트가 실제로 차지하는 영역을 세밀하게 조절할 수 있습니다.

# 권한 설정
앱을 실행하기 위해서는 **'다른 앱 위에 표시'** 권한이 필요합니다. 첫 권한 허용 이후에는 별도 설정 없이 자동으로 실행됩니다.

# 기능 설명
1. 텍스트 컴포넌트를 추가할 수 있습니다. (영역의 제한으로 인해 최대 6개)
2. 전체 숨김 기능을 제공합니다. 텍스트 컴포넌트를 제외한 모든 UI 를 숨깁니다.
3. 텍스트 사이즈, 행간, 실제 영역을 동적으로 조절할 수 있습니다.
4. 텍스트 내용을 변경할 수 있습니다.
5. 텍스트 위치를 미세하게 조절하는 핸들을 제공합니다.

# 특이사항
* 키보드를 통해 텍스트 내용을 변경한 경우 반드시 **SAVE 버튼**을 눌러주셔야 합니다. SAVE 버튼을 누르기 전까지는 키보드 권한을 ShowMeTheText 앱에서 가지고 있기 때문에 다른 앱에서 키보드를 사용할 수 없습니다.
* 앱에서 제공하는 텍스트 컴포넌트는 **includeFontPadding** 옵션이 **true** 로 설정되어 있습니다.
* 앱 사용 중 오류 발생 시, 오류화면에서 제공하는 에러로그를 **Github Issue**에 남겨주시면 빠르게 확인 및 수정이 가능합니다.
* Custom font (*.ttf) 를 사용하고 있을 경우 **FontStyle** 필드를 통해 적용이 가능합니다.

# Preview
### 1. Sample Text Unit 생성
<p>
    <img src="https://user-images.githubusercontent.com/27072782/161017353-ca1a36c5-3222-4f5f-80e1-4db7bccb712b.jpg" width="300">
</p>

### 2. Text Unit 세부 설정
<p>
    <img src="https://user-images.githubusercontent.com/27072782/161017829-690add61-f158-473a-a5eb-ce316fe317bb.jpg" width="300">
</p>

### 3. 최소화 모드
</p>
    <img src="https://user-images.githubusercontent.com/27072782/161018001-36df5861-b0f1-4f0c-af57-b5cc83e03105.jpg" width="300">
</p>

# 라이선스
```
Copyright (c) 2022-present NAVER Z Corp.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
