Create table if not exists comments (
    id int primary key auto_increment,
    product_id int not null,
    user_id int not null,
    content nvarchar(255) not null,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    foreign key (product_id) references products(id),
    foreign key (user_id) references users(id)
);