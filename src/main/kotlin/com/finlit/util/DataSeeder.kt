package com.finlit.util

import com.finlit.model.entity.Category
import com.finlit.model.entity.QuizQuestion
import com.finlit.model.entity.TransactionType
import com.finlit.repository.CategoryRepository
import com.finlit.repository.QuizRepository
import com.finlit.repository.UserRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DataSeeder(
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
    private val quizRepository: QuizRepository
) : ApplicationRunner {

    @Transactional
    override fun run(args: ApplicationArguments?) {
        seedCategories()
        seedQuizQuestions()
    }

    private fun seedCategories() {
        val users = userRepository.findAll()
        users.forEach { user ->
            if (categoryRepository.findByUserId(user.id!!).isEmpty()) {
                categoryRepository.saveAll(listOf(
                    Category("Gaji", "💼", TransactionType.INCOME, user),
                    Category("Freelance", "💻", TransactionType.INCOME, user),
                    Category("Investasi", "📈", TransactionType.INCOME, user),
                    Category("Makan & Minum", "🍔", TransactionType.EXPENSE, user),
                    Category("Transport", "🚌", TransactionType.EXPENSE, user),
                    Category("Pendidikan", "📚", TransactionType.EXPENSE, user),
                    Category("Hiburan", "🎮", TransactionType.EXPENSE, user),
                    Category("Kesehatan", "💊", TransactionType.EXPENSE, user),
                    Category("Belanja", "🛍️", TransactionType.EXPENSE, user),
                    Category("Lainnya", "📦", TransactionType.EXPENSE, user)
                ))
            }
        }
    }

    private fun seedQuizQuestions() {
        if (quizRepository.count() > 0) return

        val q1 = QuizQuestion(
            question = "Apa yang dimaksud dengan budgeting dalam pengelolaan keuangan?",
            optionA = "Proses meminjam uang dari bank",
            optionB = "Proses merencanakan pengeluaran dan pemasukan secara terstruktur",
            optionC = "Proses investasi di pasar saham",
            optionD = "Proses membayar hutang",
            correctAnswer = "B",
            explanation = "Budgeting adalah proses membuat rencana keuangan dengan mengalokasikan pendapatan untuk berbagai kebutuhan dan tujuan secara terstruktur.",
            category = "Budgeting"
        )

        val q2 = QuizQuestion(
            question = "Berapa persen dari penghasilan yang idealnya untuk tabungan menurut aturan 50/30/20?",
            optionA = "50%",
            optionB = "30%",
            optionC = "20%",
            optionD = "10%",
            correctAnswer = "C",
            explanation = "Aturan 50/30/20: 50% untuk kebutuhan, 30% untuk keinginan, dan 20% untuk tabungan dan investasi.",
            category = "Tabungan"
        )

        val q3 = QuizQuestion(
            question = "Apa yang dimaksud dengan dana darurat?",
            optionA = "Uang yang disimpan untuk liburan",
            optionB = "Uang cadangan untuk kebutuhan mendesak yang tidak terduga",
            optionC = "Uang untuk membeli barang mewah",
            optionD = "Uang pinjaman dari bank",
            correctAnswer = "B",
            explanation = "Dana darurat adalah tabungan yang disiapkan untuk menghadapi situasi darurat tak terduga seperti sakit atau kehilangan pekerjaan.",
            category = "Tabungan"
        )

        val q4 = QuizQuestion(
            question = "Berapa bulan pengeluaran yang direkomendasikan untuk dana darurat?",
            optionA = "1-2 bulan",
            optionB = "3-6 bulan",
            optionC = "10-12 bulan",
            optionD = "2 tahun",
            correctAnswer = "B",
            explanation = "Para ahli keuangan merekomendasikan dana darurat sebesar 3-6 bulan pengeluaran bulanan untuk perlindungan finansial yang memadai.",
            category = "Tabungan"
        )

        val q5 = QuizQuestion(
            question = "Apa perbedaan utama antara saham dan obligasi?",
            optionA = "Saham adalah surat hutang, obligasi adalah kepemilikan perusahaan",
            optionB = "Saham adalah kepemilikan perusahaan, obligasi adalah surat hutang",
            optionC = "Keduanya adalah jenis mata uang",
            optionD = "Keduanya sama, hanya beda nama",
            correctAnswer = "B",
            explanation = "Saham merepresentasikan kepemilikan sebagian perusahaan, sedangkan obligasi adalah surat hutang yang diterbitkan perusahaan atau pemerintah.",
            category = "Investasi"
        )

        val q6 = QuizQuestion(
            question = "Apa yang dimaksud dengan inflasi dalam ekonomi?",
            optionA = "Kenaikan nilai mata uang",
            optionB = "Penurunan harga barang dan jasa",
            optionC = "Kenaikan harga barang dan jasa secara umum dan terus-menerus",
            optionD = "Peningkatan produksi barang",
            correctAnswer = "C",
            explanation = "Inflasi adalah kondisi di mana harga barang dan jasa secara umum mengalami kenaikan terus-menerus, menyebabkan daya beli uang menurun.",
            category = "Investasi"
        )

        val q7 = QuizQuestion(
            question = "Strategi investasi diversifikasi bertujuan untuk?",
            optionA = "Memaksimalkan keuntungan dalam satu jenis investasi",
            optionB = "Menyebar risiko dengan berinvestasi di berbagai instrumen",
            optionC = "Menghindari pajak investasi",
            optionD = "Mempercepat pertumbuhan modal",
            correctAnswer = "B",
            explanation = "Diversifikasi adalah strategi menyebar investasi ke berbagai instrumen untuk mengurangi risiko kerugian total.",
            category = "Investasi"
        )

        val q8 = QuizQuestion(
            question = "Apa yang dimaksud dengan bunga majemuk atau compound interest?",
            optionA = "Bunga yang dihitung hanya dari modal awal",
            optionB = "Bunga yang dihitung dari modal awal ditambah bunga yang sudah terkumpul",
            optionC = "Bunga pinjaman dari bank",
            optionD = "Denda keterlambatan pembayaran",
            correctAnswer = "B",
            explanation = "Bunga majemuk dihitung dari modal awal PLUS bunga yang sudah terakumulasi, sehingga investasi tumbuh secara eksponensial.",
            category = "Investasi"
        )

        val q9 = QuizQuestion(
            question = "Apa risiko utama dari kartu kredit jika tidak dikelola dengan baik?",
            optionA = "Meningkatkan skor kredit",
            optionB = "Mendapatkan cashback lebih banyak",
            optionC = "Terjebak dalam hutang berbunga tinggi",
            optionD = "Mempermudah transaksi online",
            correctAnswer = "C",
            explanation = "Kartu kredit memiliki bunga yang sangat tinggi (bisa 2-3% per bulan). Jika tidak dibayar lunas, hutang bisa menumpuk dan sulit dilunasi.",
            category = "Utang & Kredit"
        )

        val q10 = QuizQuestion(
            question = "Apa yang dimaksud dengan cicilan 0% pada kartu kredit?",
            optionA = "Pembelian gratis tanpa biaya apapun",
            optionB = "Pembayaran dicicil tanpa bunga dalam periode tertentu",
            optionC = "Pinjaman tanpa syarat dari bank",
            optionD = "Diskon 100% untuk semua pembelian",
            correctAnswer = "B",
            explanation = "Cicilan 0% berarti kamu bisa mencicil pembayaran tanpa dikenakan bunga dalam periode yang ditentukan. Namun perhatikan biaya admin atau syarat lainnya.",
            category = "Utang & Kredit"
        )

        quizRepository.saveAll(listOf(q1, q2, q3, q4, q5, q6, q7, q8, q9, q10))
    }
}