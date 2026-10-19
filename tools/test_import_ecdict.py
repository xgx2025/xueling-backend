import unittest

from import_ecdict import convert, insert_statement, normalize


class ImportEcdictTest(unittest.TestCase):
    def test_normalization_preserves_phrase(self):
        self.assertEqual("can't stop", normalize("  Can't   Stop "))

    def test_sql_escapes_text(self):
        row = {
            "word": "can't", "phonetic": "", "definition": "", "translation": "n. 测试\n第二行",
            "pos": "", "collins": "", "oxford": "", "tag": "", "bnc": "0", "frq": "0",
            "exchange": "", "detail": "", "audio": "",
        }
        statement = insert_statement([convert(row, "test-version")])
        self.assertIn("can\\'t", statement)
        self.assertIn("测试\\n第二行", statement)
        self.assertIn("ON DUPLICATE KEY UPDATE", statement)


if __name__ == "__main__":
    unittest.main()
