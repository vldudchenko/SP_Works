/**
 * Система управления библиотекой
 * JavaScript версия приложения для управления коллекцией книг
 */

class Book {
    /**
     * Конструктор класса Book
     * @param {number} id - Уникальный идентификатор книги
     * @param {string} title - Название книги
     * @param {string} author - Автор книги
     * @param {string} publisher - Издательство
     * @param {number} year - Год издания
     * @param {number} pages - Количество страниц
     * @param {number} price - Цена книги
     * @param {string} bindingType - Тип переплета
     */
    constructor(id, title, author, publisher, year, pages, price, bindingType) {
        this._id = id;
        this._title = title;
        this._author = author;
        this._publisher = publisher;
        this._year = year;
        this._pages = pages;
        this._price = price;
        this._bindingType = bindingType;
    }
    
    // Геттеры
    getId() { return this._id; }
    getTitle() { return this._title; }
    getAuthor() { return this._author; }
    getPublisher() { return this._publisher; }
    getYear() { return this._year; }
    getPages() { return this._pages; }
    getPrice() { return this._price; }
    getBindingType() { return this._bindingType; }
    
    // Сеттеры
    setId(id) { this._id = id; }
    setTitle(title) { this._title = title; }
    setAuthor(author) { this._author = author; }
    setPublisher(publisher) { this._publisher = publisher; }
    setYear(year) { this._year = year; }
    setPages(pages) { this._pages = pages; }
    setPrice(price) { this._price = price; }
    setBindingType(bindingType) { this._bindingType = bindingType; }
    
    /**
     * Строковое представление книги
     * @returns {string}
     */
    toString() {
        return `Книга [ID: ${this._id}, Название: '${this._title}', ` +
               `Автор: ${this._author}, Издательство: '${this._publisher}', ` +
               `Год: ${this._year}, Страниц: ${this._pages}, ` +
               `Цена: ${this._price.toFixed(2)} руб., Переплет: '${this._bindingType}']`;
    }
    
    /**
     * Сравнение книг
     * @param {Book} other - Другая книга
     * @returns {boolean}
     */
    equals(other) {
        if (!(other instanceof Book)) return false;
        return this._id === other._id &&
               this._title === other._title &&
               this._author === other._author &&
               this._publisher === other._publisher &&
               this._year === other._year &&
               this._pages === other._pages &&
               this._price === other._price &&
               this._bindingType === other._bindingType;
    }
}

class BookManager {
    /**
     * Конструктор менеджера книг
     */
    constructor() {
        this._books = [];
        this._nextId = 1;
    }
    
    /**
     * Добавить книгу в коллекцию
     * @param {Book} book - Книга для добавления
     */
    addBook(book) {
        this._books.push(book);
    }
    
    /**
     * Создать и добавить книгу
     * @param {string} title - Название
     * @param {string} author - Автор
     * @param {string} publisher - Издательство
     * @param {number} year - Год
     * @param {number} pages - Страницы
     * @param {number} price - Цена
     * @param {string} bindingType - Переплет
     * @returns {Book} - Созданная книга
     */
    createBook(title, author, publisher, year, pages, price, bindingType) {
        const book = new Book(
            this._nextId++,
            title,
            author,
            publisher,
            year,
            pages,
            price,
            bindingType
        );
        this.addBook(book);
        return book;
    }
    
    /**
     * Получить все книги
     * @returns {Book[]}
     */
    getAllBooks() {
        return [...this._books]; // Возвращаем копию
    }
    
    /**
     * Удалить книгу по ID
     * @param {number} id - ID книги для удаления
     * @returns {boolean} - true если книга была удалена
     */
    removeBookById(id) {
        const index = this._books.findIndex(book => book.getId() === id);
        if (index > -1) {
            this._books.splice(index, 1);
            return true;
        }
        return false;
    }
    
    /**
     * Найти книги по автору
     * @param {string} author - Автор для поиска
     * @returns {Book[]}
     */
    getBooksByAuthor(author) {
        return this._books.filter(book => 
            book.getAuthor().toLowerCase().includes(author.toLowerCase())
        );
    }
    
    /**
     * Найти книги по издательству
     * @param {string} publisher - Издательство для поиска
     * @returns {Book[]}
     */
    getBooksByPublisher(publisher) {
        return this._books.filter(book => 
            book.getPublisher().toLowerCase().includes(publisher.toLowerCase())
        );
    }
    
    /**
     * Найти книги после указанного года
     * @param {number} year - Год
     * @returns {Book[]}
     */
    getBooksAfterYear(year) {
        return this._books.filter(book => book.getYear() > year);
    }
    
    /**
     * Получить статистику
     * @returns {Object}
     */
    getStatistics() {
        if (this._books.length === 0) {
            return {
                totalBooks: 0,
                yearRange: [0, 0],
                priceRange: [0, 0],
                publishers: 0,
                authors: 0
            };
        }
        
        const years = this._books.map(book => book.getYear()).filter(year => year > 0);
        const prices = this._books.map(book => book.getPrice()).filter(price => price > 0);
        const publishers = new Set(this._books.map(book => book.getPublisher()));
        const authors = new Set(this._books.map(book => book.getAuthor()));
        
        return {
            totalBooks: this._books.length,
            yearRange: years.length > 0 ? [Math.min(...years), Math.max(...years)] : [0, 0],
            priceRange: prices.length > 0 ? [Math.min(...prices), Math.max(...prices)] : [0, 0],
            publishers: publishers.size,
            authors: authors.size
        };
    }
}

// Глобальные переменные
let bookManager = new BookManager();

/**
 * Инициализация приложения
 */
function init() {
    // Создаем демонстрационные данные
    createSampleBooks();
    updateStatistics();
    showAllBooks();
}

/**
 * Создать демонстрационные книги
 */
function createSampleBooks() {
    const sampleBooks = [
        ["Война и мир", "Лев Толстой", "Эксмо", 1869, 1274, 1200.0, "Твердый"],
        ["Мастер и Маргарита", "Михаил Булгаков", "АСТ", 1967, 384, 450.0, "Мягкий"],
        ["Евгений Онегин", "Александр Пушкин", "Эксмо", 1833, 352, 300.0, "Твердый"],
        ["Преступление и наказание", "Федор Достоевский", "АСТ", 1866, 592, 550.0, "Твердый"],
        ["Анна Каренина", "Лев Толстой", "Эксмо", 1877, 864, 800.0, "Твердый"],
        ["Собачье сердце", "Михаил Булгаков", "АСТ", 1925, 192, 250.0, "Мягкий"],
        ["Капитанская дочка", "Александр Пушкин", "Эксмо", 1836, 256, 280.0, "Твердый"],
        ["Идиот", "Федор Достоевский", "АСТ", 1869, 640, 600.0, "Твердый"],
        ["Стихотворения", "Александр Пушкин", "Эксмо", 1826, 480, 400.0, "Твердый"],
        ["Братья Карамазовы", "Федор Достоевский", "АСТ", 1880, 824, 750.0, "Твердый"]
    ];
    
    sampleBooks.forEach(bookData => {
        bookManager.createBook(...bookData);
    });
}

/**
 * Добавить новую книгу
 */
function addBook() {
    const title = document.getElementById('bookTitle').value.trim();
    const author = document.getElementById('bookAuthor').value.trim();
    const publisher = document.getElementById('bookPublisher').value.trim();
    const year = parseInt(document.getElementById('bookYear').value);
    const pages = parseInt(document.getElementById('bookPages').value);
    const price = parseFloat(document.getElementById('bookPrice').value);
    const bindingType = document.getElementById('bookBinding').value;
    
    // Валидация
    if (!title || !author || !publisher) {
        showError('Пожалуйста, заполните все обязательные поля');
        return;
    }
    
    if (isNaN(year) || year <= 0) {
        showError('Год издания должен быть положительным числом');
        return;
    }
    
    if (isNaN(pages) || pages <= 0) {
        showError('Количество страниц должно быть положительным числом');
        return;
    }
    
    if (isNaN(price) || price < 0) {
        showError('Цена должна быть неотрицательным числом');
        return;
    }
    
    // Создаем книгу
    const book = bookManager.createBook(title, author, publisher, year, pages, price, bindingType);
    
    // Очищаем форму
    clearForm();
    
    // Обновляем статистику и показываем результат
    updateStatistics();
    showSuccess(`Книга "${title}" успешно добавлена!`);
    showAllBooks();
}

/**
 * Очистить форму
 */
function clearForm() {
    document.getElementById('bookTitle').value = '';
    document.getElementById('bookAuthor').value = '';
    document.getElementById('bookPublisher').value = '';
    document.getElementById('bookYear').value = '';
    document.getElementById('bookPages').value = '';
    document.getElementById('bookPrice').value = '';
    document.getElementById('bookBinding').value = '';
}

/**
 * Поиск по автору
 */
function searchByAuthor() {
    const author = document.getElementById('searchAuthor').value.trim();
    if (!author) {
        showError('Введите автора для поиска');
        return;
    }
    
    const books = bookManager.getBooksByAuthor(author);
    displayBooks(books, `Книги автора: ${author}`);
}

/**
 * Поиск по издательству
 */
function searchByPublisher() {
    const publisher = document.getElementById('searchPublisher').value.trim();
    if (!publisher) {
        showError('Введите издательство для поиска');
        return;
    }
    
    const books = bookManager.getBooksByPublisher(publisher);
    displayBooks(books, `Книги издательства: ${publisher}`);
}

/**
 * Поиск по году
 */
function searchByYear() {
    const year = parseInt(document.getElementById('searchYear').value);
    if (isNaN(year) || year <= 0) {
        showError('Введите корректный год');
        return;
    }
    
    const books = bookManager.getBooksAfterYear(year);
    displayBooks(books, `Книги после ${year} года`);
}

/**
 * Показать все книги
 */
function showAllBooks() {
    const books = bookManager.getAllBooks();
    displayBooks(books, 'Все книги в библиотеке');
}

/**
 * Удалить книгу по ID
 * @param {number} bookId - ID книги для удаления
 */
function deleteBook(bookId) {
    if (confirm('Вы уверены, что хотите удалить эту книгу?')) {
        const success = bookManager.removeBookById(bookId);
        if (success) {
            updateStatistics();
            // Обновляем отображение в зависимости от текущего поиска
            const searchAuthor = document.getElementById('searchAuthor').value.trim();
            const searchPublisher = document.getElementById('searchPublisher').value.trim();
            const searchYear = document.getElementById('searchYear').value.trim();
            
            if (searchAuthor) {
                searchByAuthor();
            } else if (searchPublisher) {
                searchByPublisher();
            } else if (searchYear) {
                searchByYear();
            } else {
                showAllBooks();
            }
            showSuccess('Книга успешно удалена!');
        } else {
            showError('Ошибка при удалении книги');
        }
    }
}

/**
 * Отобразить книги
 * @param {Book[]} books - Массив книг
 * @param {string} title - Заголовок
 */
function displayBooks(books, title) {
    const resultsDiv = document.getElementById('searchResults');
    
    if (books.length === 0) {
        resultsDiv.innerHTML = `
            <div class="error">
                <strong>${title}:</strong> Книги не найдены.
            </div>
        `;
        return;
    }
    
    let html = `
        <div class="success">
            <strong>${title}:</strong> Найдено ${books.length} книг
        </div>
        <div class="table-container">
            <table class="books-table">
                <thead>
                    <tr>
                        <th>Название</th>
                        <th>Автор</th>
                        <th>Издательство</th>
                        <th>Год</th>
                        <th>Страниц</th>
                        <th>Цена (руб.)</th>
                        <th>Переплет</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
    `;
    
    books.forEach((book, index) => {
        html += `
            <tr>
                <td class="book-title-cell">${book.getTitle()}</td>
                <td>${book.getAuthor()}</td>
                <td>${book.getPublisher()}</td>
                <td>${book.getYear()}</td>
                <td>${book.getPages()}</td>
                <td>${book.getPrice().toFixed(2)}</td>
                <td>${book.getBindingType()}</td>
                <td>
                    <button class="btn btn-danger btn-small" onclick="deleteBook(${book.getId()})">
                        Удалить
                    </button>
                </td>
            </tr>
        `;
    });
    
    html += `
                </tbody>
            </table>
        </div>
    `;
    resultsDiv.innerHTML = html;
}

/**
 * Обновить статистику
 */
function updateStatistics() {
    const stats = bookManager.getStatistics();
    
    document.getElementById('totalBooks').textContent = stats.totalBooks;
    document.getElementById('totalPublishers').textContent = stats.publishers;
    document.getElementById('totalAuthors').textContent = stats.authors;
    document.getElementById('yearRange').textContent = 
        stats.yearRange[0] === 0 ? '-' : `${stats.yearRange[0]} - ${stats.yearRange[1]}`;
}

/**
 * Показать сообщение об ошибке
 * @param {string} message - Сообщение
 */
function showError(message) {
    const resultsDiv = document.getElementById('searchResults');
    resultsDiv.innerHTML = `<div class="error">${message}</div>`;
}

/**
 * Показать сообщение об успехе
 * @param {string} message - Сообщение
 */
function showSuccess(message) {
    const resultsDiv = document.getElementById('searchResults');
    resultsDiv.innerHTML = `<div class="success">${message}</div>`;
}

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', init);
