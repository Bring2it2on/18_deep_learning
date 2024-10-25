from transformers import MBartForConditionalGeneration, MBart50TokenizerFast
from konlpy.tag import Okt

# 모델 load
model = MBartForConditionalGeneration.from_pretrained("facebook/mbart-large-50-many-to-many-mmt")

# Tokenizer load
tokenizer = MBart50TokenizerFast.from_pretrained("facebook/mbart-large-50-many-to-many-mmt")

# 토크나이저 한국어 설정
tokenizer.src_lang="ko_KR"

# Okt 토크나이저 초기화
okt = Okt()

def perform_translation(text: str, lang: str) -> str:

    # 전처리
    # Okt로 한글 토큰화
    tokens = okt.morphs(text)
    preprocessed_text = ' '.join(tokens)

    # MBart 모델에 맞게 인코딩
    encoded_ko = tokenizer(preprocessed_text, return_tensors="pt")

    # 추론
    generated_tokens = model.generate(
        **encoded_ko,
        forced_bos_token_id=tokenizer.lang_code_to_id[lang]
    )

    # 후처리
    # 번역 결과 decoding
    translated_text = tokenizer.batch_decode(generated_tokens, skip_special_tokens=True)

    return translated_text[0]
