DROP TABLE IF EXISTS tb_account CASCADE;
DROP TABLE IF EXISTS tb_transaction CASCADE;
DROP TYPE IF EXISTS account_type;
DROP TYPE IF EXISTS transaction_type;

SET DateStyle TO DMY;

CREATE TYPE account_type AS ENUM(
	'CHECKING',
	'SAVINGS',
    'SALARY',
    'BUSINESS',
    'STUDENT',
    'INVESTMENT'
);

CREATE TYPE transaction_type AS ENUM (
 	'DEPOSIT',
    'WITHDRAWAL',
    'TRANSFER'
);

CREATE TABLE tb_account (
	number serial PRIMARY KEY,
	type account_type NOT NULL,
	balance numeric(20, 2),
	opening_date date,
	holder varchar(40) NOT NULL,
	holder_phone varchar(11) NOT NULL,
	holder_birthdate date NOT NULL,
	holder_cpf varchar(11) NOT NULL,
	password varchar(20) NOT NULL,
	active boolean
);

CREATE TABLE tb_transaction (
	id serial PRIMARY KEY,
	type transaction_type NOT NULL,
	value numeric (20, 2) NOT NULL,
	transaction_date date,
	transfer_account serial,
	origin_account serial,

	CONSTRAINT fk_account FOREIGN KEY (origin_account) references tb_account(number),
	CONSTRAINT fk_account_transfer FOREIGN KEY (transfer_account) references tb_account(number)
);

