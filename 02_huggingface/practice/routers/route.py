import io
from fastapi import APIRouter
from PIL import Image
from collections import Counter, defaultdict
from fastapi import UploadFile, File
from fastapi.responses import HTMLResponse
from services.service import query, draw_boxes, convert_image_to_base64

router = APIRouter()

@router.post("/uploadfile")
async def create_upload_file(file: UploadFile = File(...)):
    # 이미지 파일 읽기
    image_bytes = await file.read()
    
    # API 호출
    output = query(image_bytes)
    
    # 이미지 생성
    image = Image.open(io.BytesIO(image_bytes))

    # 바운딩 박스 그리기
    image_with_boxes = draw_boxes(image.copy(), output)

    # 원본 이미지와 바운딩 박스 이미지 Base64로 변환
    original_image_base64 = convert_image_to_base64(image)
    image_with_boxes_base64 = convert_image_to_base64(image_with_boxes)

    # 레이블 개수와 평균 신뢰도 계산
    label_counts = Counter(item['label'] for item in output)
    label_scores = defaultdict(list)

    for item in output:
        label_scores[item['label']].append(item['score'])
    
    # 평균 신뢰도 계산
    average_scores = {label: sum(scores) / len(scores) for label, scores in label_scores.items()}

    # 결과를 HTML로 반환
    result_html = """
    <h2>레이블 개수 및 평균 신뢰도</h2>
    <ul>
    """
    for label, count in label_counts.items():
        avg_score = average_scores[label]
        result_html += f"<li>'{label}' 레이블의 개수: {count}, 평균 신뢰도: {avg_score:.2f}</li>"
    result_html += "</ul>"

    # 원본 이미지와 바운딩 박스 이미지 표시
    result_html += f"""
    <h2>업로드된 이미지</h2>
    <img src="data:image/png;base64,{original_image_base64}" alt="Original Image" style="max-width: 60%; height: auto;">
    
    <h2>바운딩 박스 이미지</h2>
    <img src="data:image/png;base64,{image_with_boxes_base64}" alt="Image with Bounding Boxes" style="max-width: 60%; height: auto;">
    <br><br><br><br>
    """

    return HTMLResponse(content=result_html)

@router.get("/")
async def main():
    content = """
    <h1>이미지 업로드</h1>
    <form action="/uploadfile/" enctype="multipart/form-data" method="post">
    <input name="file" type="file" />
    <input type="submit" value="Upload" />
    </form>
    """
    return HTMLResponse(content=content)

