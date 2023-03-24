"""
To read a file, store the contents in a variable, and perform the two tasks (generate questions and answers, and generate a summary) 
using the OpenAI API, you can use the following code sample. Make sure you have the OpenAI Python library installed (pip install openai).
"""

import openai

# Replace 'your_api_key' with your actual API key
openai.api_key = "your_api_key"

def generate_questions_and_answers(text, max_questions=5):
    prompt = f"Given the following text, generate {max_questions} questions and their respective answers:\n\n{text}\n\nQuestions and Answers:"

    response = openai.Completion.create(
        engine="text-davinci-002",
        prompt=prompt,
        max_tokens=200,
        n=1,
        stop=None,
        temperature=0.7, """Play around with this number for accuracy"""
    )

    generated_text = response.choices[0].text.strip()
    return generated_text.split("\n")

def generate_summary(text):
    prompt = f"Please provide a concise summary of the following text:\n\n{text}\n\nSummary:"

    response = openai.Completion.create(
        engine="text-davinci-002",
        prompt=prompt,
        max_tokens=100,
        n=1,
        stop=None,
        temperature=0.5, """Play around with this number for accuracy"""
    )

    return response.choices[0].text.strip()

# Read the file and store its contents in the text variable
with open("your_file.txt", "r") as file:
    text = file.read()

"""Generate questions and answers from the text"""
questions_and_answers = generate_questions_and_answers(text)

"""Generate the summary from the text"""
summary = generate_summary(text)

"""Print the generated questions and answers"""
print("Questions and Answers:")
for i, qa in enumerate(questions_and_answers, 1):
    print(f"{i}. {qa}")

"""Print the generated summary"""
print("\nSummary:")
print(summary)


"""
Replace `your_file.txt` with the name of the file you want to read. This code reads the contents of the file and stores it in the
`text` variable. Then, it uses the `generate_questions_and_answers` and `generate_summary` functions to generate questions and answers 
and a summary of the text, respectively.
"""

"""
Don't forget to replace "your_api_key" with your actual API key for the code to work.
"""

"""
Also, you can adjust the number of generated questions by changing the `max_questions` parameter when calling the
`generate_questions_and_answers` function.
"""
