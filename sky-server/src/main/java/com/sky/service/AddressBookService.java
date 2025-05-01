package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

/**
 * Class name: AddressBookService
 * Package: com.sky.service
 * Description:
 *
 * @Create: 2025/4/30 19:33
 * @Author: jay
 * @Version: 1.0
 */

public interface AddressBookService {


    /**
     * 查询所有地址信息
     * @return
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 根据id查询地址信息
     * @param id
     * @return
     */
    AddressBook getById(Long id);

    void save(AddressBook addressBook);

    void update(AddressBook addressBook);

    void setDefault(AddressBook addressBook);

    void deleteById(Long id);
}
